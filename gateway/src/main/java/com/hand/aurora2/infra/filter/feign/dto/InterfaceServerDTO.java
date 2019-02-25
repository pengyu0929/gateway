package com.hand.aurora2.infra.filter.feign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 服务配置DTO
 *
 * @author like.zhang@hand-china.com 2018/09/25 11:48
 */
@ApiModel("服务配置DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterfaceServerDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键")
    private Long interfaceServerId;
    @ApiModelProperty(value = "租户ID", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "服务代码", required = true)
    @NotBlank
    private String serverCode;
    @ApiModelProperty(value = "服务名称", required = true)
    @NotBlank
    private String serverName;
    @ApiModelProperty(value = "服务类型，代码：HITF.SERVICE_TYPE", required = true)
    @NotBlank
    private String serviceType;
    @ApiModelProperty(value = "服务地址", required = true)
    @NotBlank
    private String domainUrl;
    @ApiModelProperty(value = "认证模式，代码：HITF.AUTH_TYPE", required = true)
    @NotBlank
    private String authType;
    @ApiModelProperty(value = "授权模式，代码：HITF.GRANT_TYPE")
    private String grantType;
    @ApiModelProperty(value = "获取Token的URL")
    private String accessTokenUrl;
    @ApiModelProperty(value = "客户端ID")
    private String clientId;
    @ApiModelProperty(value = "客户端密钥")
    private String clientSecret;
    @ApiModelProperty(value = "认证用户名")
    private String authUsername;
    @ApiModelProperty(value = "认证密码")
    private String authPassword;
    @ApiModelProperty(value = "权限范围")
    private String scope;
    @ApiModelProperty(value = "SOAP命名空间")
    private String soapNamespace;
    @ApiModelProperty(value = "SOAP参数前缀标识")
    private String soapElementPrefix;
    @ApiModelProperty(value = "SOAP加密类型")
    private String soapWssPasswordType;
    @ApiModelProperty(value = "校验用户名")
    private String soapUsername;
    @ApiModelProperty(value = "校验密码")
    private String soapPassword;
    @ApiModelProperty(value = "是否启用。1启用，0未启用", required = true)
    @NotNull
    private Integer enabledFlag;

    public Long getInterfaceServerId() {
        return interfaceServerId;
    }

    public void setInterfaceServerId(Long interfaceServerId) {
        this.interfaceServerId = interfaceServerId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSoapNamespace() {
        return soapNamespace;
    }

    public void setSoapNamespace(String soapNamespace) {
        this.soapNamespace = soapNamespace;
    }

    public String getSoapElementPrefix() {
        return soapElementPrefix;
    }

    public void setSoapElementPrefix(String soapElementPrefix) {
        this.soapElementPrefix = soapElementPrefix;
    }

    public String getSoapWssPasswordType() {
        return soapWssPasswordType;
    }

    public void setSoapWssPasswordType(String soapWssPasswordType) {
        this.soapWssPasswordType = soapWssPasswordType;
    }

    public String getSoapUsername() {
        return soapUsername;
    }

    public void setSoapUsername(String soapUsername) {
        this.soapUsername = soapUsername;
    }

    public String getSoapPassword() {
        return soapPassword;
    }

    public void setSoapPassword(String soapPassword) {
        this.soapPassword = soapPassword;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    @Override
    public String toString() {
        return "InterfaceServerDTO{" +
                "interfaceServerId=" + interfaceServerId +
                ", tenantId=" + tenantId +
                ", serverCode='" + serverCode + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", domainUrl='" + domainUrl + '\'' +
                ", authType='" + authType + '\'' +
                ", grantType='" + grantType + '\'' +
                ", accessTokenUrl='" + accessTokenUrl + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", authUsername='" + authUsername + '\'' +
                ", authPassword='" + authPassword + '\'' +
                ", scope='" + scope + '\'' +
                ", soapNamespace='" + soapNamespace + '\'' +
                ", soapElementPrefix='" + soapElementPrefix + '\'' +
                ", soapWssPasswordType='" + soapWssPasswordType + '\'' +
                ", soapUsername='" + soapUsername + '\'' +
                ", soapPassword='" + soapPassword + '\'' +
                ", enabledFlag=" + enabledFlag +
                '}';
    }
}
