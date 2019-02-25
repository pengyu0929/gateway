package com.hand.aurora2.api.filter.childfilter;

import com.hand.aurora2.api.filter.HelperFilter;
import com.hand.aurora2.api.service.PermissionService;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.PermissionDO;
import com.hand.aurora2.domain.RequestContext;
import org.springframework.stereotype.Component;

@Component
public class GetPermissionFilter implements HelperFilter {

    private PermissionService permissionService;

    public GetPermissionFilter(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public int filterOrder() {
        return 20;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        String key = context.getRequestKey();
        PermissionDO permissionDO = permissionService.selectPermissionByRequest(key);
        if (permissionDO == null) {
            context.response.setStatus(CheckState.PERMISSION_MISMATCH);
            context.response.setMessage("This request mismatch any permission");
            return false;
        } else if (permissionDO.getWithin()) {
            context.response.setStatus(CheckState.PERMISSION_WITH_IN);
            context.response.setMessage("No access to within interface");
            return false;
        } else {
            context.setPermission(permissionDO);
        }
        return true;
    }

}