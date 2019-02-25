package com.hand.aurora2.infra.cache.l1.guava;

import com.hand.aurora2.infra.cache.l1.L1Cache;

public class GuavaL1Cache extends L1Cache {

    public GuavaL1Cache(org.springframework.cache.Cache cache) {
        super(cache);
    }

}