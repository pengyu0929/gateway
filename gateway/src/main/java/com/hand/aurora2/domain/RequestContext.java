package com.hand.aurora2.domain;

import io.choerodon.core.oauth.CustomUserDetails;
import org.springframework.cloud.config.client.ZuulRoute;

import javax.servlet.http.HttpServletRequest;

public class RequestContext {

    public final CheckRequest request;

    public final CheckResponse response;

    private String requestKey;

    private PermissionDO permission;

    private ZuulRoute route;

    private String trueUri;

    private CustomUserDetails customUserDetails;

    private String lovCode;

    private HttpServletRequest servletRequest;

    public RequestContext(CheckRequest request, CheckResponse builder) {
        this.request = request;
        this.response = builder;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public PermissionDO getPermission() {
        return permission;
    }

    public void setPermission(PermissionDO permission) {
        this.permission = permission;
    }

    public ZuulRoute getRoute() {
        return route;
    }

    public void setRoute(ZuulRoute route) {
        this.route = route;
    }

    public String getTrueUri() {
        return trueUri;
    }

    public void setTrueUri(String trueUri) {
        this.trueUri = trueUri;
    }

    public CustomUserDetails getCustomUserDetails() {
        return customUserDetails;
    }

    public void setCustomUserDetails(CustomUserDetails customUserDetails) {
        this.customUserDetails = customUserDetails;
    }

    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "request=" + request +
                ", response=" + response +
                ", requestKey='" + requestKey + '\'' +
                ", permission=" + permission +
                ", route=" + route +
                ", lovCode='" + lovCode +
                ", trueUri='" + trueUri + '\'' +
                ", customUserDetails=" + customUserDetails +
                '}';
    }
}

