package com.hand.aurora2.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hand.aurora2.api.service.GetUserDetailsService;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.CustomUserDetailsWithResult;
import com.hand.aurora2.infra.config.HelperProperties;
import io.choerodon.core.oauth.CustomUserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetUserDetailsServiceImpl implements GetUserDetailsService {


    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserDetailsService.class);

    private static final String PRINCIPAL = "principal";

    private static final String OAUTH2REQUEST = "oauth2Request";

    private static final String ADDITION_INFO = "additionInfo";

    private static final String USER_ID = "userId";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestTemplate restTemplate;

    private HelperProperties helperProperties;

    public GetUserDetailsServiceImpl(RestTemplate restTemplate, HelperProperties helperProperties) {
        this.restTemplate = restTemplate;
        this.helperProperties = helperProperties;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CustomUserDetailsWithResult getUserDetails(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(helperProperties.getOauthInfoUri(), HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                CustomUserDetails userDetails = extractPrincipal(objectMapper.readValue(responseEntity.getBody(), Map.class));
                return new CustomUserDetailsWithResult(userDetails, CheckState.SUCCESS_PASS_SITE);
            } else {
                return new CustomUserDetailsWithResult(CheckState.PERMISSION_GET_USE_DETAIL_FAILED,
                        "Get customUserDetails error from oauth-server, token: " + token + " response: " + responseEntity);
            }
        } catch (RestClientException e) {
            LOGGER.warn("Get customUserDetails error from oauth-server, token: {}", token, e);
            return new CustomUserDetailsWithResult(CheckState.PERMISSION_ACCESS_TOKEN_EXPIRED,
                    "Access_token is expired or invalid, Please re-login and set correct access_token by HTTP header 'Authorization'");
        } catch (IOException e) {
            return new CustomUserDetailsWithResult(CheckState.EXCEPTION_GATEWAY_HELPER,
                    "Gateway helper error happened: " + e.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private CustomUserDetails extractPrincipal(Map<String, Object> map) {
        boolean isClientOnly = false;
        Map<String, Object> oauth2request = null;
        if (map.get(OAUTH2REQUEST) != null) {
            oauth2request = (Map) map.get(OAUTH2REQUEST);
            if (oauth2request.get("grantType").equals("client_credentials")) {
                isClientOnly = true;
            }
        }
        if (map.get(PRINCIPAL) != null) {
            map = (Map) map.get(PRINCIPAL);
        }

        return setUserDetails(map, isClientOnly, oauth2request);
    }

    @SuppressWarnings("unchecked")
    private CustomUserDetails setUserDetails(final Map<String, Object> map, boolean isClientOnly, Map<String, Object> oauth2request) {
        if (map.containsKey(USER_ID)) {
            CustomUserDetails user = new CustomUserDetails((String) map.get("username"),
                    "unknown password", Collections.emptyList());
            user.setOrganizationId((long) (Integer) map.get("organizationId"));
            if (map.get(USER_ID) != null) {
                user.setUserId((long) (Integer) map.get(USER_ID));
                user.setLanguage((String) map.get("language"));
                user.setAdmin((Boolean) map.get("admin"));
                user.setTimeZone((String) map.get("timeZone"));
                if (map.get("email") != null) {
                    user.setEmail((String) map.get("email"));
                }
                // Add by qingsheng.chen@hand-china.com
                // Map -> Access Token
                if (map.get("roleId") != null) {
                    user.setRoleId(Long.valueOf(String.valueOf(map.get("roleId"))));
                }
                if (map.get("roleIds") != null) {
                    Object userRoleIds = map.get("roleIds");
                    if (userRoleIds instanceof List) {
                        List<Object> roleIds = (List) userRoleIds;
                        user.setRoleIds(roleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                    }
                }
                if (map.get("tenantId") != null) {
                    user.setTenantId(Long.valueOf(String.valueOf(map.get("tenantId"))));
                }
                if (map.get("tenantIds") != null) {
                    Object tenantIds = map.get("tenantIds");
                    if (tenantIds instanceof List) {
                        List<Object> userTenantIds = (List) tenantIds;
                        user.setTenantIds(userTenantIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                    }
                }
                // End
            }
            if (isClientOnly) {
                user.setClientId((long) (Integer) map.get("clientId"));
                user.setClientName((String) map.get("clientName"));
                user.setClientAccessTokenValiditySeconds((Integer) map.get("clientAccessTokenValiditySeconds"));
                user.setClientRefreshTokenValiditySeconds((Integer) map.get("clientRefreshTokenValiditySeconds"));
                user.setClientAuthorizedGrantTypes((Collection<String>) map.get("clientAuthorizedGrantTypes"));
                user.setClientAutoApproveScopes((Collection<String>) map.get("clientAutoApproveScopes"));
                user.setClientRegisteredRedirectUri((Collection<String>) map.get("clientRegisteredRedirectUri"));
                user.setClientResourceIds((Collection<String>) map.get("clientResourceIds"));
                user.setClientScope((Collection<String>) map.get("clientScope"));
            }/* FIXME : 添加ClientName */ else if (oauth2request != null) {
                user.setClientName(oauth2request.containsKey("clientId") ? String.valueOf(oauth2request.get("clientId")) : null);
            }
            try {
                if (map.get(ADDITION_INFO) != null) {
                    user.setAdditionInfo((Map) map.get(ADDITION_INFO));
                }
            } catch (Exception e) {
                LOGGER.warn("Parser addition info error:{}", e);
            }
            return user;
        }
        return null;
    }
}
