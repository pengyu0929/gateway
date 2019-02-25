package com.hand.aurora2.infra.autoconfigure;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@ComponentScan(value = {
    "org.hzero.gateway.helper",
    "io.choerodon.gateway.helper.api",
    "io.choerodon.gateway.helper.domain",
    "io.choerodon.gateway.helper.infra",
})
@EnableCaching
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class GatewayHelperAutoConfiguration {

}
