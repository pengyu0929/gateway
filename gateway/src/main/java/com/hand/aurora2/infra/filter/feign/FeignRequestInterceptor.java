package com.hand.aurora2.infra.filter.feign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;

import javax.annotation.PostConstruct;
import java.util.Collections;


/**
 * 拦截feign请求，为requestTemplate加上oauth jwt token请求头
 * @author mingwei.liu@hand-china.com
 */
public class FeignRequestInterceptor implements RequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignRequestInterceptor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String OAUTH_TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_JWT = "Jwt_Token";

    private Signer jwtSigner;

    private CustomUserDetails defaultUserDetails;

    public FeignRequestInterceptor(Signer jwtSigner) {
        this.jwtSigner = jwtSigner;
    }

    @PostConstruct
    private void init() {
        defaultUserDetails = new CustomUserDetails("default", "unknown", Collections.emptyList());
        defaultUserDetails.setUserId(0L);
        defaultUserDetails.setOrganizationId(0L);
        defaultUserDetails.setLanguage("zh_CN");
        defaultUserDetails.setTimeZone("CCT");
    }

    @Override
    public void apply(RequestTemplate template) {
        String jwtToken = this.getJwtToken();
        template.header(HEADER_JWT, jwtToken);
    }

    /**
     * 获取JWT Token
     * @return
     */
    private String getJwtToken() {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }

        /**
         * 获取Jwt Token组成部分
         */
        CustomUserDetails details = DetailsHelper.getUserDetails();
        if (details == null) {
            LOGGER.info("can't get customUserDetails, and use defaultCustomUserDetails");
            details = this.defaultUserDetails;
        }


        /**
         * 编码Jwt Token
         */
        String jwtToken = null;
        try {
            String token = OBJECT_MAPPER.writeValueAsString(details);
            jwtToken = OAUTH_TOKEN_PREFIX + JwtHelper.encode(token, jwtSigner).getEncoded();
        } catch (JsonProcessingException e) {
            LOGGER.warn("error happened when add JWT : {}", e);
        }

        LOGGER.info("====================>Jwt token added is {}<====================",
                jwtToken);

        return jwtToken;
    }

}
