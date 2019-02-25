package com.hand.aurora2.infra.autoconfigure;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.retry.annotation.EnableRetry;

@ComponentScan(value = {
        "com.hand.aurora2.infra",
        "com.hand.aurora2.api",
        "com.hand.aurora2.domain",
})
@EnableRetry
@EnableCaching
@EnableZuulProxy
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Configuration
public class GatewayAutoConfiguration {

}
