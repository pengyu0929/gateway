package com.hand.aurora2.infra.filter.feign;

import com.hand.aurora2.infra.filter.feign.dto.InterfaceServerDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * <p>
 * 描述
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/10/7
 */
@FeignClient(value = "hzero-interface", configuration = {FeignTokenConfiguration.class})
public interface InterfaceServerFeign {
    @GetMapping("/v1/{organizationId}/interface-servers/by-code")
    InterfaceServerDTO queryInterfaceServerByCode(@PathVariable("organizationId") Long organizationId,
                                                  @RequestParam("serverCode") String serverCode);
}