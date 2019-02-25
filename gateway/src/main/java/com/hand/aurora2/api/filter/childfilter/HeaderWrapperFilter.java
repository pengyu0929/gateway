package com.hand.aurora2.api.filter.childfilter;

import com.hand.aurora2.infra.config.GatewayHelperProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_JWT;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

public class HeaderWrapperFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderWrapperFilter.class);

    private GatewayHelperProperties gatewayHelperProperties;


    public HeaderWrapperFilter(GatewayHelperProperties gatewayHelperProperties) {
        this.gatewayHelperProperties = gatewayHelperProperties;
    }

    private static final int HEADER_WRAPPER_FILTER = -1;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return HEADER_WRAPPER_FILTER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = (String) request.getAttribute(HEADER_JWT);
        if (StringUtils.isEmpty(token)) {
            LOGGER.info("Request get empty jwt , request uri: {} method: {}", request.getRequestURI(), request.getMethod());
        } else {
            ctx.addZuulRequestHeader(HEADER_JWT, token);
            if (gatewayHelperProperties.isEnabledJwtLog()) {
                LOGGER.info("Request get jwt , request uri: {} method: {} JWT: {}",
                        request.getRequestURI(), request.getMethod(), token);
            }
        }
        return null;
    }
}

