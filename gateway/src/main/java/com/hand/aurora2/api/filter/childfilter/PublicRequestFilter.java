package com.hand.aurora2.api.filter.childfilter;

import com.hand.aurora2.api.filter.HelperFilter;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.RequestContext;
import org.springframework.stereotype.Component;

@Component
public class PublicRequestFilter implements HelperFilter {

    @Override
    public int filterOrder() {
        return 30;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return context.getPermission().getPublicAccess();
    }

    @Override
    public boolean run(RequestContext context) {
        context.response.setStatus(CheckState.SUCCESS_PUBLIC_ACCESS);
        context.response.setMessage("Have access to this 'publicAccess' interface, permission: " + context.getPermission());
        return false;
    }

}

