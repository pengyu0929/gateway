package com.hand.aurora2.infra.filter.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import io.choerodon.gateway.helper.permission.PermissionProperties;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/10/22
 */
@Configuration
//@EnableConfigurationProperties(PermissionProperties.class)
public class FeignTokenConfiguration {
//    private PermissionProperties permissionProperties;
//
//    @Autowired
//    public FeignTokenConfiguration(PermissionProperties permissionProperties) {
//        this.permissionProperties = permissionProperties;
//    }


    /**
     * 自动添加Jwt Token
     *
     * @return
     */
//    @Bean
//    public RequestInterceptor feignAutoJwtTokenRequestInterceptor() {
//        return new FeignRequestInterceptor(new MacSigner(permissionProperties.getJwt().getKey()));
//    }

    /**
     * 日志记录
     *
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
