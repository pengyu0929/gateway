package com.hand.aurora2.api.service.impl;

import static com.hand.aurora2.api.filter.childfilter.GetRequestRouteFilter.REQUEST_KEY_SEPARATOR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hand.aurora2.api.service.PermissionService;
import com.hand.aurora2.domain.PermissionDO;
import com.hand.aurora2.infra.mapper.PermissionMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author bojiangzhou Mark: 增加从缓存中获取权限，获取不到再从数据库获取
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionMapper permissionMapper;

    private RedisHelper redisHelper;

    private ObjectMapper mapper = BaseConstants.MAPPER;

    public PermissionServiceImpl(PermissionMapper permissionMapper, RedisHelper redisHelper) {
        this.permissionMapper = permissionMapper;
        this.redisHelper = redisHelper;
    }

    /**
     * Cacheable 设置使用二级缓存
     * 先通过method和service从数据库中查询权限；
     * 如果匹配到多条权限，则排序计算出匹配度最高的权限
     */
    @Override
    @Cacheable(value = "permission", key = "'choerodon:permission:'+#requestKey", unless = "#result == null")
    public PermissionDO selectPermissionByRequest(String requestKey) {
        String[] request = requestKey.split(REQUEST_KEY_SEPARATOR);
        String uri = request[0];
        String method = request[1];
        String serviceName = request[2];

        List<PermissionDO> permissionDOS = selectPermissions(serviceName, method);

        List<PermissionDO> matchPermissions = permissionDOS.stream().filter(t -> matcher.match(t.getPath(), uri))
                .sorted((PermissionDO o1, PermissionDO o2) -> {
                    Comparator<String> patternComparator = matcher.getPatternComparator(uri);
                    return patternComparator.compare(o1.getPath(), o2.getPath());
                }).collect(Collectors.toList());
        int matchSize = matchPermissions.size();
        if (matchSize < 1) {
            return null;
        } else {
            PermissionDO bestMatchPermission = matchPermissions.get(0);
            if (matchSize > 1) {
                LOGGER.info("Request: {} match multiply permission: {}, the best match is: {}",
                        uri, matchPermissions, bestMatchPermission.getPath());
            }
            return bestMatchPermission;
        }
    }

    private List<PermissionDO> selectPermissions(String serviceName, String method) {
        List<PermissionDO> permissionDOS;

        String permissionKey = PermissionDO.generateKey(serviceName, method);
        // 先从缓存获取
        Set<String> permissions = redisHelper.zSetReverseRange(permissionKey, 0L, -1L);
        if (CollectionUtils.isNotEmpty(permissions)) {
            permissionDOS = new ArrayList<>(permissions.size());
            try {
                for (String permission : permissions) {
                    permissionDOS.add(mapper.readValue(permission, PermissionDO.class));
                }
            } catch (IOException e) {
                LOGGER.error("deserialize json error.");
            }
        }
        // 查询数据库
        else {
            permissionDOS = permissionMapper.selectPermissionByMethodAndService(method, serviceName);
        }

        return permissionDOS;
    }


}
