package com.hand.aurora2.infra.mapper;

import com.hand.aurora2.domain.PermissionDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限mapper
 *
 * @author flyleft
 */
public interface PermissionMapper extends BaseMapper<PermissionDO> {


    List<PermissionDO> selectPermissionByMethodAndService(@Param("method") String method,
                                                          @Param("service") String service);

    List<Long> selectSourceIdsByUserIdAndPermission(@Param("memberId") long memberId,
                                                    @Param("memberType") String memberType,
                                                    @Param("permissionId") long permissionId,
                                                    @Param("sourceType") String sourceType);

    Boolean projectEnabled(@Param("sourceId") Long sourceId);

    Boolean organizationEnabled(@Param("sourceId") Long sourceId);
}

