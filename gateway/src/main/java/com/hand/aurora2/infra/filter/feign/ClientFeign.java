package com.hand.aurora2.infra.filter.feign;

import com.hand.aurora2.infra.filter.feign.dto.ClientDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * HIAM 远程服务调用
 *
 * @author xingxing.wu@hand-china.com 2018/09/21 15:45
 */
@FeignClient(value = "hzero-iam", fallback = ClientFeignFallback.class, configuration = {FeignTokenConfiguration.class})
public interface ClientFeign {

    /**
     * 查询客户端
     *
     * @param organization_id 租户ID
     * @param client_name     客户端名称
     * @return 返回值
     */
    @GetMapping("/v1/organizations/{organization_id}/clients/query_by_name")
    ClientDTO queryByName(@PathVariable("organization_id") Long organization_id, @RequestParam("client_name") String client_name);
}

@Component
class ClientFeignFallback implements ClientFeign {
    @Override
    public ClientDTO queryByName(@PathVariable("organization_id") Long organization_id, @RequestParam("client_name") String client_name) {
        return null;
    }
}
