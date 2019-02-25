package com.hand.aurora2.infra.filter.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2018/09/21 11:02
 */
public class ClientDTO extends AuditDomain {

    private Long id;
    @Size(
            min = 1,
            max = 32,
            message = "error.name.size"
    )
    @NotNull(
            message = "error.clientName.null"
    )
    private String name;
    private Long organizationId;
    private String resourceIds;
    @NotNull(
            message = "error.secret.null"
    )
    private String secret;
    private String scope;
    @NotNull(
            message = "error.authorizedGrantTypes.null"
    )
    private String authorizedGrantTypes;
    private String webServerRedirectUri;
    private Long accessTokenValidity;
    private Long refreshTokenValidity;
    private String additionalInformation;
    private String autoApprove;
    private Long objectVersionNumber;
    @JsonIgnore
    private String param;

    public ClientDTO() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getResourceIds() {
        return this.resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthorizedGrantTypes() {
        return this.authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getWebServerRedirectUri() {
        return this.webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public Long getAccessTokenValidity() {
        return this.accessTokenValidity;
    }

    public void setAccessTokenValidity(Long accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public Long getRefreshTokenValidity() {
        return this.refreshTokenValidity;
    }

    public void setRefreshTokenValidity(Long refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String getAdditionalInformation() {
        return this.additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAutoApprove() {
        return this.autoApprove;
    }

    public void setAutoApprove(String autoApprove) {
        this.autoApprove = autoApprove;
    }

    public Long getObjectVersionNumber() {
        return this.objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", organizationId=" + organizationId +
                ", resourceIds='" + resourceIds + '\'' +
                ", secret='" + secret + '\'' +
                ", scope='" + scope + '\'' +
                ", authorizedGrantTypes='" + authorizedGrantTypes + '\'' +
                ", webServerRedirectUri='" + webServerRedirectUri + '\'' +
                ", accessTokenValidity=" + accessTokenValidity +
                ", refreshTokenValidity=" + refreshTokenValidity +
                ", additionalInformation='" + additionalInformation + '\'' +
                ", autoApprove='" + autoApprove + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                ", param='" + param + '\'' +
                '}';
    }
}
