package com.hand.aurora2.api.filter.childfilter;

import com.hand.aurora2.api.filter.HelperFilter;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * loginAccess请求的权限校验
 *
 * @author bojiangzhou Mark: 针对SQL类型的LOV请求，需校验权限
 */
@Component
public class LoginAccessRequestFilter implements HelperFilter {

    @Override
    public int filterOrder() {
        return 60;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        // lov 访问需验证权限
        if (StringUtils.isNotEmpty(context.getLovCode())) {
            return true;
        }
        if (context.getPermission().getLoginAccess() && context.getCustomUserDetails() != null) {
            context.response.setStatus(CheckState.SUCCESS_LOGIN_ACCESS);
            context.response.setMessage("Have access to this 'loginAccess' interface, permission: " + context.getPermission());
            return false;
        }
        return true;
    }

}
