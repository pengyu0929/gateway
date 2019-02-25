package com.hand.aurora2.infra.filter.feign;

import com.hand.aurora2.infra.filter.feign.dto.InterfaceDTO;
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
public interface InterfaceFeign {
    @GetMapping("/v1/{organizationId}/interfaces/by-code")
    InterfaceDTO queryInterfaceByCode(@PathVariable("organizationId") Long organizationId,
                                      @RequestParam("serverCode") String serverCode,
                                      @RequestParam("interfaceCode") String interfaceCode);
}
