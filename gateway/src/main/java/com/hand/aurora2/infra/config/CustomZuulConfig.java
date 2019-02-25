package com.hand.aurora2.infra.config;

import com.hand.aurora2.api.filter.RootServletFilter;
import com.hand.aurora2.api.filter.childfilter.HeaderWrapperFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.config.client.MemoryRouteLocator;
import org.springframework.cloud.config.client.RouterOperator;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableConfigurationProperties({GatewayHelperProperties.class, HelperProperties.class})
public class CustomZuulConfig {
    @Value("${choerodon.gateway.allowed.origin:*}")
    private String allowedOrigin;

    @Bean
    public RouteLocator memoryRouterOperator(ServerProperties server, ZuulProperties zuulProperties) {
        return new MemoryRouteLocator(server.getServletPrefix(), zuulProperties);
    }

    @Bean(name = "handRouterOperator")
    public RouterOperator routerOperator(ApplicationEventPublisher publisher,
                                         RouteLocator routeLocator) {
        return new RouterOperator(publisher, routeLocator);
    }

    @Bean
    public RootServletFilter rootServletFilter(GatewayHelperProperties gatewayHelperProperties){
        return new RootServletFilter(gatewayHelperProperties);
    }

    @Bean
    public FilterRegistrationBean gatewayHelperFilterRegistrationBean(RootServletFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("rootServletFilter");
        registration.setOrder(1);
        return registration;
    }
    /**
     * 解决跨域问题
     *
     * @return 跨域声明
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(allowedOrigin);
        config.addAllowedHeader("*");
        config.setMaxAge(18000L);
        config.addAllowedMethod("*");
        //添加response暴露的header
        String[] responseHeader =
                {"date", "content-encoding", "server", "etag", "vary", "Cache-Control", "Last-Modified",
                        "content-type", "transfer-encoding", "connection", "x-application-context"};
        config.setExposedHeaders(Arrays.asList(responseHeader));
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public Signer jwtSigner(HelperProperties helperProperties) {
        return new MacSigner(helperProperties.getJwtKey());
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HeaderWrapperFilter headerWrapperFilter(GatewayHelperProperties gatewayHelperProperties) {
        return new HeaderWrapperFilter(gatewayHelperProperties);
    }
}
