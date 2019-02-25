package com.hand.aurora2.api.service;

import com.hand.aurora2.domain.PermissionDO;

public interface PermissionService {
    PermissionDO selectPermissionByRequest(String requestKey);
}
