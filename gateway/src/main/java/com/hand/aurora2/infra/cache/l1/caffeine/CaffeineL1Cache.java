package com.hand.aurora2.infra.cache.l1.caffeine;

import com.hand.aurora2.infra.cache.l1.L1Cache;

public class CaffeineL1Cache extends L1Cache {

    public CaffeineL1Cache(org.springframework.cache.Cache cache) {
        super(cache);
    }


}
