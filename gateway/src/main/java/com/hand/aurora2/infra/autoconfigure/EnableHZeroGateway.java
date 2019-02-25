package com.hand.aurora2.infra.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author bojiangzhou 2018/11/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GatewayAutoConfiguration.class)
public @interface EnableHZeroGateway {

}
