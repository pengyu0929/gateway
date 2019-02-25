package com.hand.aurora2.api.filter.childfilter;

import com.hand.aurora2.api.filter.HelperFilter;
import com.hand.aurora2.domain.CheckState;
import com.hand.aurora2.domain.PermissionDO;
import com.hand.aurora2.domain.RequestContext;
import com.hand.aurora2.infra.mapper.PermissionMapper;
import com.hand.aurora2.infra.mapper.PermissionPlusMapper;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 普通接口(除公共接口，loginAccess接口，内部接口以外的接口)
 * 普通用户(超级管理员之外用户)的权限校验
 *
 * @author bojiangzhou Mark: 路径上租户API支持 organizationId 参数；角色权限查询权限变更。
 */
@Component
public class CommonRequestCheckFilter implements HelperFilter {

    private static final String PROJECT_PATH_ID = "project_id";

    private static final String ORG_PATH_ID = "organization_id";
    private static final String ORG_PATH_ID_HUMP = "organizationId";

    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionMapper permissionMapper;
    private PermissionPlusMapper permissionPlusMapper;

    public CommonRequestCheckFilter(PermissionMapper permissionMapper, PermissionPlusMapper permissionPlusMapper) {
        this.permissionMapper = permissionMapper;
        this.permissionPlusMapper = permissionPlusMapper;
    }

    public void setPermissionMapper(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public int filterOrder() {
        return 80;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        PermissionDO permission = context.getPermission();
        Long memberId;
        String memberType;
        List<Long> sourceIds = new ArrayList<>();
        String permissionCode = StringUtils.defaultString(context.getLovCode(), permission.getCode());
        if (context.getCustomUserDetails().getClientId() != null) {
            memberId = context.getCustomUserDetails().getClientId();
            memberType = "client";
            List<Long> longs = permissionPlusMapper.selectSourceIdsByUserIdAndPermissionEffective(
                    memberId, memberType,
                    permissionCode, permission.getFdLevel());
            sourceIds.addAll(longs);
        }

        if (context.getCustomUserDetails().getUserId() != null) {
            memberId = context.getCustomUserDetails().getUserId();
            memberType = "user";
            List<Long> longs = permissionPlusMapper.selectSourceIdsByUserIdAndPermissionEffective(
                    memberId, memberType,
                    permissionCode, permission.getFdLevel());
            sourceIds.addAll(longs);
        }
        if (sourceIds.isEmpty()) {
            context.response.setStatus(CheckState.PERMISSION_NOT_PASS);
            context.response.setMessage("No access to this interface");
        } else if (ResourceLevel.SITE.value().equals(permission.getFdLevel())) {
            context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
            context.response.setMessage("Have access to this 'site-level' interface, permission: " + context.getPermission());
        } else if (ResourceLevel.PROJECT.value().equals(permission.getFdLevel())) {
            checkProjectPermission(context, sourceIds, permission.getPath());
        } else if (ResourceLevel.ORGANIZATION.value().equals(permission.getFdLevel())) {
            checkOrgPermission(context, sourceIds, permission.getPath());
        }
        return true;
    }

    private void checkProjectPermission(final RequestContext context,
                                        final List<Long> sourceIds,
                                        final String matchPath) {
        Long projectId = parseProjectOrOrgIdFromUri(context.getTrueUri(), matchPath, PROJECT_PATH_ID);
        if (projectId == null) {
            context.response.setStatus(CheckState.API_ERROR_PROJECT_ID);
            context.response.setMessage("Project interface must have 'project_id' in path");
        } else {
            Boolean isEnabled = permissionMapper.projectEnabled(projectId);
            if (isEnabled != null && !isEnabled) {
                context.response.setStatus(CheckState.PERMISSION_DISABLED_PROJECT);
                context.response.setMessage("The project has been disabled, projectId: " + projectId);
            } else if (sourceIds.stream().anyMatch(t -> t.equals(projectId))) {
                context.response.setStatus(CheckState.SUCCESS_PASS_PROJECT);
                context.response.setMessage("Have access to this 'project-level' interface, permission: " + context.getPermission());
            } else {
                context.response.setStatus(CheckState.PERMISSION_NOT_PASS_PROJECT);
                context.response.setMessage("No access to this this project, projectId: " + projectId);
            }
        }
    }

    private void checkOrgPermission(final RequestContext context,
                                    final List<Long> sourceIds,
                                    final String matchPath) {
        Long orgId = Optional.ofNullable(parseProjectOrOrgIdFromUri(context.getTrueUri(), matchPath, ORG_PATH_ID_HUMP))
                .orElse(parseProjectOrOrgIdFromUri(context.getTrueUri(), matchPath, ORG_PATH_ID));
        if (orgId == null) {
            context.response.setStatus(CheckState.API_ERROR_ORG_ID);
            context.response.setMessage("Organization interface must have 'organizationId' or 'organization_id' in path");
        } else {
            List<Long> tenantIds = DetailsHelper.getUserDetails().getTenantIds();
            boolean accessOrg = tenantIds.contains(orgId);
            // Mark: 改成判断当前用户可访问租户列表即可，避免查询一次数据库
            // Boolean isEnabled = permissionMapper.organizationEnabled(orgId);
            if (accessOrg) {
                context.response.setStatus(CheckState.SUCCESS_PASS_ORG);
                context.response.setMessage("Have access to this 'organization-level' interface, permission: " + context.getPermission());
            } else {
                context.response.setStatus(CheckState.PERMISSION_NOT_PASS_ORG);
                context.response.setMessage("No access to this this organization, organizationId: " + orgId);
            }
        }
    }

    private Long parseProjectOrOrgIdFromUri(final String uri, final String matchPath, String id) {
        Map<String, String> map = matcher.extractUriTemplateVariables(matchPath, uri);
        if (map.size() < 1) {
            return null;
        }
        String value = map.get(id);
        if (value != null) {
            return Long.parseLong(value);
        }
        return null;
    }

}
