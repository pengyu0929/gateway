package com.hand.aurora2.infra.cache.l2;

import org.springframework.cache.CacheManager;

public interface L2CacheManager extends CacheManager {

    L2Cache getL2Cache(String name, String spec);

}
