package com.hand.aurora2.api.filter.childfilter;

import com.hand.aurora2.api.filter.HelperFilter;
import com.hand.aurora2.api.service.GetUserDetailsService;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.CustomUserDetailsWithResult;
import com.hand.aurora2.domain.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GetUserDetailsFilter implements HelperFilter {

    private GetUserDetailsService getUserDetailsService;

    public GetUserDetailsFilter(GetUserDetailsService getUserDetailsService) {
        this.getUserDetailsService = getUserDetailsService;
    }

    public void setGetUserDetailsService(GetUserDetailsService getUserDetailsService) {
        this.getUserDetailsService = getUserDetailsService;
    }

    @Override
    public int filterOrder() {
        return 40;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean run(RequestContext context) {
        String accessToken = context.request.accessToken;
        if (StringUtils.isEmpty(accessToken)) {
            context.response.setStatus(CheckState.PERMISSION_ACCESS_TOKEN_NULL);
            context.response.setMessage("Access_token is empty, Please login and set access_token by HTTP header 'Authorization'");
            return false;
        }
        CustomUserDetailsWithResult result = getUserDetailsService.getUserDetails(accessToken);
        if (result.getCustomUserDetails() == null) {
            context.response.setStatus(result.getState());
            context.response.setMessage(result.getMessage());
            return false;
        }
        context.setCustomUserDetails(result.getCustomUserDetails());
        return true;
    }

}

