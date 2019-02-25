package com.hand.aurora2.api.filter.childfilter;

import com.hand.aurora2.api.filter.HelperFilter;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.RequestContext;
import com.hand.aurora2.infra.config.HelperProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class PermissionDisableOrSkipFilter implements HelperFilter {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private HelperProperties helperProperties;

    public PermissionDisableOrSkipFilter(HelperProperties helperProperties) {
        this.helperProperties = helperProperties;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        if (!helperProperties.getPermission().getEnabled()) {
            context.response.setStatus(CheckState.SUCCESS_PERMISSION_DISABLED);
            context.response.setMessage("Permission check disabled");
            return false;
        }
        if (helperProperties.getPermission().getSkipPaths().stream()
                .anyMatch(t -> matcher.match(t, context.request.uri))) {
            context.response.setStatus(CheckState.SUCCESS_SKIP_PATH);
            context.response.setMessage("This request match skipPath, skipPaths: " + helperProperties.getPermission().getSkipPaths());
            return false;
        }
        return true;
    }
}

