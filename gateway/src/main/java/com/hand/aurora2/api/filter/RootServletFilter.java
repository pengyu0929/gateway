package com.hand.aurora2.api.filter;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_JWT;
import static io.choerodon.core.variable.RequestVariableHolder.HEADER_TOKEN;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.aurora2.domain.CheckRequest;
import com.hand.aurora2.domain.CheckResponse;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.RequestContext;
import com.hand.aurora2.infra.config.GatewayHelperProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Mark:加入401返回码
 *
 * @author bojiangzhou 2018/12/25
 */
@Component
public class RootServletFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootServletFilter.class);

    private List<HelperFilter> helperFilters;

    private static final String CONFIG_ENDPOINT = "/choerodon/config";

    private final AntPathMatcher matcher = new AntPathMatcher();

    private GatewayHelperProperties gatewayHelperProperties;

    private static final Set<CheckState> UNAUTHORIZED = new HashSet<>(Arrays.asList(
            CheckState.PERMISSION_ACCESS_TOKEN_NULL,
            CheckState.PERMISSION_ACCESS_TOKEN_INVALID,
            CheckState.PERMISSION_ACCESS_TOKEN_EXPIRED,
            CheckState.PERMISSION_GET_USE_DETAIL_FAILED
    ));

    public RootServletFilter(GatewayHelperProperties gatewayHelperProperties) {
        this.gatewayHelperProperties = gatewayHelperProperties;
    }

    public RootServletFilter(Optional<List<HelperFilter>> optionalHelperFilters) {
        helperFilters = optionalHelperFilters.orElseGet(Collections::emptyList)
                .stream()
                .sorted(Comparator.comparing(HelperFilter::filterOrder))
                .collect(Collectors.toList());
    }

    public void setHelperFilters(List<HelperFilter> helperFilters) {
        this.helperFilters = helperFilters;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //将gatewayfilter中的逻辑放在这里
        boolean shouldSkipHelper = Arrays.stream(gatewayHelperProperties.getHelperSkipPaths())
                .anyMatch(t -> matcher.match(t, req.getRequestURI()));
        if (shouldSkipHelper) {
            chain.doFilter(req, res);
            return;
        }
        if (CONFIG_ENDPOINT.equals(req.getRequestURI())) {
            chain.doFilter(request, res);
            return;
        }
        RequestContext requestContext = new RequestContext(new CheckRequest(Optional.ofNullable(req.getHeader(HEADER_TOKEN)).orElse("Bearer " + req.getParameter("access_token")),
                req.getRequestURI(), req.getMethod().toLowerCase()), new CheckResponse());
        requestContext.setServletRequest(req);
        CheckResponse checkResponse = requestContext.response;
        try {
            for (HelperFilter t : helperFilters) {
                if (t.shouldFilter(requestContext) && !t.run(requestContext)) {
                    break;
                }
            }
            request.setCharacterEncoding("utf-8");
            res.setHeader("Content-type", "text/html;charset=UTF-8");
            res.setCharacterEncoding("utf-8");

            res.setHeader("request-status", checkResponse.getStatus().name());
            res.setHeader("request-code", checkResponse.getStatus().getCode());

            if (checkResponse.getJwt() != null) {
                res.setHeader(HEADER_JWT, checkResponse.getJwt());
            }
            if (checkResponse.getMessage() != null) {
                res.setHeader("request-message", checkResponse.getMessage());
            }
            if (checkResponse.getStatus().getValue() < 300) {
                res.setStatus(200);
                LOGGER.debug("Request 200, context: {}", requestContext);
                chain.doFilter(request, res);
            } else if (UNAUTHORIZED.contains(checkResponse.getStatus())) {
                res.setStatus(401);
                setGatewayHelperFailureResponse(401, res);
                LOGGER.info("Request 401, context: {}", requestContext);
            } else if (checkResponse.getStatus().getValue() < 500) {
                res.setStatus(403);
                setGatewayHelperFailureResponse(403, res);
                LOGGER.info("Request 403, context: {}", requestContext);
            } else {
                res.setStatus(500);
                setGatewayHelperFailureResponse(500, res);
                LOGGER.info("Request 500, context: {}", requestContext);
            }
            try (PrintWriter out = res.getWriter()) {
                out.flush();
            }
        } catch (Exception e) {
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            res.setCharacterEncoding("utf-8");
            LOGGER.info("Check permission error", e);
            try (PrintWriter out = res.getWriter()) {
                out.println(e.getMessage());
                out.flush();
            }
        }
        try (PrintWriter out = res.getWriter()) {
            out.flush();
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
    private void setGatewayHelperFailureResponse(int statusCode, HttpServletResponse res) {
        res.setCharacterEncoding("utf-8");
        res.setContentType("application/xhtml+xml");
        try (PrintWriter out = res.getWriter()) {
            if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                res.setStatus(statusCode);
                String message = "<oauth>" +
                        "<error_description>Full authentication is required to access this resource</error_description>" +
                        "<error>unauthorized</error>" +
                        "</oauth>";
                LOGGER.warn("gateway-helper response unauthorized, {}", res.getHeader("request-message"));
                out.println(message);
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                res.setStatus(statusCode);
                String message = "<oauth>" +
                        "<error_description>" + res.getHeaders("request-status") + "</error_description>" +
                        "<error>forbidden</error>" +
                        "</oauth>";
                LOGGER.warn("gateway-helper response forbidden, {}", res.getHeaders("request-message"));
                out.println(message);
            } else {
                res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                String message = "<oauth>" +
                        "<error_description>" + res.getHeaders("request-status") + "</error_description>" +
                        "<error>error</error>" +
                        "</oauth>";
                LOGGER.warn("gateway-helper response error, {}", res.getHeaders("request-message"));
                out.println(message);
            }
            out.flush();
        } catch (Exception e) {
        }
    }
}
