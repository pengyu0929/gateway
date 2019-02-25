package com.hand.aurora2.infra.filter.feign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * 接口配置DTO
 *
 * @author like.zhang@hand-china.com 2018/09/26 10:12
 */
@ApiModel("接口配置DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterfaceDTO extends AuditDomain {
    @ApiModelProperty("表ID，主键")
    @Id
    @GeneratedValue
    private Long interfaceId;
    @ApiModelProperty(value = "租户ID",required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "服务配置ID")
    private Long interfaceServerId;
    @ApiModelProperty(value = "接口代码",required = true)
    @NotBlank
    private String interfaceCode;
    @ApiModelProperty(value = "接口名称",required = true)
    @NotBlank
    private String interfaceName;
    @ApiModelProperty(value = "接口地址")
    private String interfaceUrl;
    @ApiModelProperty(value = "发布类型，代码：HITF.SERVICE_TYPE",required = true)
    @NotBlank
    private String publishType;
    @ApiModelProperty(value = "映射类，处理请求参数及响应格式的映射")
    private String mappingClass;
    @ApiModelProperty(value = "请求方式，代码：HITF.REQUEST_METHOD")
    private String requestMethod;
    @ApiModelProperty(value = "请求头")
    private String requestHeader;
    @ApiModelProperty(value = "是否启用，1代表启用，0代表禁用",required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "SOAP版本，代码：HITF.SOAP_VERSION")
    private String soapVersion;
    @ApiModelProperty(value = "SOAPACTION")
    private String soapAction;
    @ApiModelProperty(value = "是否记录调用详情",required = true)
    @NotNull
    private Integer invokeRecordDetails;
    @ApiModelProperty(value = "状态，代码：HITF.INTERFACE_STATUS, ENABLED/DISABLED/DISABLE_INPROGRESS",required = true)
    @NotBlank
    private String status;
    @ApiModelProperty(value = "备注说明")
    private String remark;

    public Long getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(Long interfaceId) {
        this.interfaceId = interfaceId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getInterfaceServerId() {
        return interfaceServerId;
    }

    public void setInterfaceServerId(Long interfaceServerId) {
        this.interfaceServerId = interfaceServerId;
    }

    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    public String getPublishType() {
        return publishType;
    }

    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }

    public String getMappingClass() {
        return mappingClass;
    }

    public void setMappingClass(String mappingClass) {
        this.mappingClass = mappingClass;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getSoapVersion() {
        return soapVersion;
    }

    public void setSoapVersion(String soapVersion) {
        this.soapVersion = soapVersion;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public Integer getInvokeRecordDetails() {
        return invokeRecordDetails;
    }

    public void setInvokeRecordDetails(Integer invokeRecordDetails) {
        this.invokeRecordDetails = invokeRecordDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "InterfaceDTO{" +
                "interfaceId=" + interfaceId +
                ", tenantId=" + tenantId +
                ", interfaceServerId=" + interfaceServerId +
                ", interfaceCode='" + interfaceCode + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", interfaceUrl='" + interfaceUrl + '\'' +
                ", publishType='" + publishType + '\'' +
                ", mappingClass='" + mappingClass + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", requestHeader='" + requestHeader + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", soapVersion='" + soapVersion + '\'' +
                ", soapAction='" + soapAction + '\'' +
                ", invokeRecordDetails=" + invokeRecordDetails +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
