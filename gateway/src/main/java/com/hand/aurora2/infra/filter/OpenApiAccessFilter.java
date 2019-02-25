package com.hand.aurora2.infra.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.aurora2.infra.filter.feign.ClientFeign;
import com.hand.aurora2.infra.filter.feign.InterfaceFeign;
import com.hand.aurora2.infra.filter.feign.InterfaceServerFeign;
import com.hand.aurora2.infra.filter.feign.InterfaceStatus;
import com.hand.aurora2.infra.filter.feign.dto.ClientDTO;
import com.hand.aurora2.infra.filter.feign.dto.InterfaceDTO;
import com.hand.aurora2.infra.filter.feign.dto.InterfaceServerDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 开放API授权验证
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/10/7
 */
public class OpenApiAccessFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiAccessFilter.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private RedisTokenStore redisTokenStore;

    /**
     * 开放API拦截URL前缀
     */
    private static final String URL_REST_PREFIX = "/v1/api/rest";
    private static final String URL_SOAP_PREFIX = "/v1/api/soap";

    /**
     * 服务配置参数
     */
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String SERVER_CODE = "serverCode";
    private static final String INTERFACE_CODE = "interfaceCode";

    /**
     * Additional Information Key in Client
     */
    private static final String AUTHORIZED_SERVERS_KEY = "AUTHORIZED_SERVERS";

    /**
     * Http bearer token key
     */
    private static final String HTTP_HEADER_BEARER_TOKEN_PREFIX = "bearer ";

    /**
     * 异常代码
     */
    private static final String EXP_OPEN_API_PERMISSION_ERR = "exp.open.api.permission.err";

    /**
     * Feign Client
     */
    private InterfaceServerFeign interfaceServerFeign;
    private InterfaceFeign interfaceFeign;
    private ClientFeign clientFeign;

    public OpenApiAccessFilter(RedisTokenStore redisTokenStore, InterfaceServerFeign interfaceServerFeign, InterfaceFeign interfaceFeign, ClientFeign clientFeign) {
        this.redisTokenStore = redisTokenStore;
        this.interfaceServerFeign = interfaceServerFeign;
        this.interfaceFeign = interfaceFeign;
        this.clientFeign = clientFeign;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        /**
         * 获取请求URL
         */
        String url = org.apache.commons.lang3.StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());

        /**
         * 如果匹配, 则进行权限校验
         */
        if (url.contains(URL_REST_PREFIX) || url.contains(URL_SOAP_PREFIX)) {
            try {
                /**
                 * 日志记录
                 */
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("==========>request url={}, request uri={}, context path={}, url substring={}<==========",
                            request.getRequestURL().toString(), request.getRequestURI(), request.getContextPath(), url);
                }

                /**
                 * 服务注册 权限控制
                 */
                checkAuth(request);
            } catch (CommonException ex) {
                LOGGER.error("OpenApiAccessFilter check Auth with exception: ", ex);

                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setCharacterEncoding("utf-8");
                ServletOutputStream outputStream = null;

                try {
                    outputStream = response.getOutputStream();
                    ExceptionResponse exceptionResponse = new ExceptionResponse(EXP_OPEN_API_PERMISSION_ERR);
                    exceptionResponse.setException(ex.getMessage());

                    outputStream.write(objectMapper.writeValueAsString(exceptionResponse).getBytes());
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }

                // 异常直接返回
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 通过SecurityContextHolder上下文获取认证信息进而获取ClientId
     *
     * 注意: 此种方式目前获取不到ClientId
     *
     * @return clientId
     */
    private String getClientIdFromSecurityContext() {
        String clientId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            clientId = oAuth2Authentication.getOAuth2Request().getClientId();

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("==========>oauth2Authentication={}, clientId={}<==========", oAuth2Authentication, clientId);
            }
        }

        return clientId;
    }

    /**
     * 通过Redis存储的Token信息获取认证信息进而获取ClientID信息
     *
     * @param request
     * @return clientId
     */
    private String getClientIdFromRedisTokenStore(HttpServletRequest request) {
        String clientId = null;

        if (this.redisTokenStore == null) {
            LOGGER.warn("==========>redis token store is null<==========");
            return clientId;
        }

        /**
         * 目前仅支持Bearer + ${access_token}模式
         * 尚不支持Form Parameter以及Query Parameter方式
         */
        String authentication = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessTokenValue = this.getAccessTokenFromAuthentication(authentication);
        OAuth2Authentication oAuth2Authentication = this.loadAuthentication(accessTokenValue);
        if (oAuth2Authentication == null) {
            LOGGER.warn("==========>oauth2 authentication from redis token store is null for access token = {}<==========",
                    accessTokenValue);
            return clientId;
        }

        /**
         * 获取Client Id
         */
        clientId = oAuth2Authentication.getOAuth2Request().getClientId();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("==========>after access token: {}, clientId={}<==========", accessTokenValue, clientId);
        }

        return clientId;
    }

    /**
     * 从认证头中获取access token
     * @param authentication
     * @return
     */
    private String getAccessTokenFromAuthentication(String authentication) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("==========>authentication: [{}]<==========", authentication);
        }
        if (StringUtils.isEmpty(authentication)) {
            LOGGER.warn("==========>Authentication Header is empty<==========");
            return null;
        }

        String bearerPrefix = authentication.substring(0, HTTP_HEADER_BEARER_TOKEN_PREFIX.length());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("==========>bearerPrefix: [{}]<==========", bearerPrefix);
        }
        if (!HTTP_HEADER_BEARER_TOKEN_PREFIX.equals(org.apache.commons.lang3.StringUtils.lowerCase(bearerPrefix))) {
            LOGGER.warn("==========>Authentication Header miss Bearer token<==========");
            return null;
        }

        String accessToken = authentication.substring(HTTP_HEADER_BEARER_TOKEN_PREFIX.length());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("==========>access token: [{}]<==========", accessToken);
        }

        return accessToken;
    }

    private OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException,
            InvalidTokenException {
        OAuth2AccessToken accessToken = redisTokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }
        else if (accessToken.isExpired()) {
            redisTokenStore.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + accessTokenValue);
        }

        OAuth2Authentication result = redisTokenStore.readAuthentication(accessToken);
        if (result == null) {
            // in case of race condition
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }

        return result;
    }


    /**
     * 是否授权
     *
     * @param request 请求
     */
    private void checkAuth(HttpServletRequest request) throws CommonException {
        /**
         * 获取OAuth2Authentication ClientId并验证
         */
        String clientId = this.getClientIdFromRedisTokenStore(request);
        if (StringUtils.isEmpty(clientId)) {
            throw new UnapprovedClientAuthenticationException("cannot found client id");
        }

        /**
         * 获取服务注册参数
         */
        String organizationId = request.getParameter(ORGANIZATION_ID);
        String serverCode = request.getParameter(SERVER_CODE);
        String interfaceCode = request.getParameter(INTERFACE_CODE);
        Assert.notNull(organizationId, "organization id cannot be null in open api");
        Assert.notNull(serverCode, "server code cannot be null in open api");
        Assert.notNull(interfaceCode, "interface code cannot be null in open api");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("==========>organizationId={}, serverCode={}, interfaceCode={}<==========",
                    organizationId, serverCode, interfaceCode);
        }

        /**
         * 获取服务信息
         */
        InterfaceServerDTO interfaceServerDTO = this.getInterfaceServer(Long.valueOf(organizationId), serverCode);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("==========>interfaceServerDTO={}<==========", interfaceServerDTO);
        }

        /**
         * 验证服务信息
         */
        this.checkInterfaceServer(interfaceServerDTO, Long.valueOf(organizationId), serverCode);


        /**
         * 验证服务绑定信息
         */
        this.checkServiceBinding(
                clientId,
                interfaceServerDTO);


        /**
         * 验证接口信息
         */
        this.checkInterface(
                Long.valueOf(organizationId),
                serverCode,
                interfaceCode);

    }

    /**
     * 获取服务信息
     *
     * @param organizationId
     * @param serverCode
     * @return
     */
    private InterfaceServerDTO getInterfaceServer(Long organizationId, String serverCode) {
        /**
         * 获取服务
         */
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("==================>GetInterfaceServer: organizationId={}, serverCode={}<==================",
                    organizationId, serverCode);
        }
        InterfaceServerDTO interfaceServerDTO = interfaceServerFeign.queryInterfaceServerByCode(
                Long.valueOf(organizationId),
                serverCode
        );
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("==================>GetInterfaceServer Return: interfaceServerDTO={}<==================",
                    interfaceServerDTO);
        }

        return interfaceServerDTO;
    }

    /**
     * 验证服务信息
     *
     * @param interfaceServerDTO
     * @param organizationId
     * @param serverCode
     */
    private void checkInterfaceServer(InterfaceServerDTO interfaceServerDTO, Long organizationId, String serverCode) {
        /**
         * 验证是否存在
         */
        if (interfaceServerDTO == null) {
            throw new CommonException(String.format("invalid server(organizationId=%s, serverCode=%s)", organizationId, serverCode));
        }

        /**
         * 验证是否启用
         */
        if (!Integer.valueOf(1).equals(interfaceServerDTO.getEnabledFlag())) {
            throw new CommonException(String.format("invalid state for server(organizationId=%s, serverCode=%s)", organizationId, serverCode));
        }
    }

    /**
     * 验证服务绑定信息
     *
     * @param clientId
     * @param interfaceServerDTO
     */
    private void checkServiceBinding(String clientId, InterfaceServerDTO interfaceServerDTO) {
        Long organizationId = interfaceServerDTO.getTenantId();
        String interfaceServerId = interfaceServerDTO.getInterfaceServerId().toString();
        String interfaceServerCode = interfaceServerDTO.getServerCode();

        // 获取客户端信息
        ClientDTO clientDTO = clientFeign.queryByName(organizationId, clientId);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("==========>client id={}, clientDTO={}<==========",
                    clientId, clientDTO.toString());
        }

        // 通过客户端获取绑定信息
        String additionalInformation = clientDTO.getAdditionalInformation();
        if (StringUtils.isEmpty(additionalInformation)) {
            throw new CommonException(String.format("client %s has no permission to access the server %s", clientId, interfaceServerCode));
        }

        // 解析Additional Information获得已绑定服务
        try {
            Map<String, String> additionalInformationMap = objectMapper.readValue(additionalInformation, Map.class);
            Set serverIdSet = StringUtils.commaDelimitedListToSet(
                    additionalInformationMap.get(AUTHORIZED_SERVERS_KEY)
            );

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("==========>serverIdSet={}<==========",
                        Arrays.toString(serverIdSet.toArray()));
            }

            if (!serverIdSet.contains(interfaceServerId)) {
                throw new CommonException(String.format("client %s has no permission to access the server %s without binding service", clientId, interfaceServerCode));
            }
        } catch (Exception ex) {
            LOGGER.error("==========>parse additional info error<==========\"", ex);
            throw new CommonException(String.format("client %s has no permission to access the server %s with internal error", clientId, interfaceServerCode));
        }
    }

    /**
     * 验证接口信息
     *
     * @param organizationId
     * @param serverCode
     * @param interfaceCode
     */
    private void checkInterface(Long organizationId,
                                String serverCode,
                                String interfaceCode) {
        /**
         * 查询接口
         */
        InterfaceDTO interfaceDTO = interfaceFeign.queryInterfaceByCode(
                organizationId,
                serverCode,
                interfaceCode);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("==========>interfaceDTO={}<==========", interfaceDTO);
        }

        /**
         * 验证是否存在
         */
        if (interfaceDTO == null) {
            throw new CommonException(String.format("invalid interface(organizationId=%s, serverCode=%s, interfaceCode=%s)", organizationId, serverCode, interfaceCode));
        }

        /**
         * 验证是否启用
         */
        if (InterfaceStatus.DISABLED.equals(interfaceDTO.getStatus())) {
            throw new CommonException(String.format("invalid state for interface(organizationId=%s, serverCode=%s, interfaceCode=%s)", organizationId, serverCode, interfaceCode));
        }
    }
}
