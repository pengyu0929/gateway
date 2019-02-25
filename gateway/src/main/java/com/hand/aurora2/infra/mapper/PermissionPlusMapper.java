package com.hand.aurora2.infra.mapper;

import com.hand.aurora2.domain.PermissionDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qingsheng.chen 2018/12/26 星期三 10:46
 */

public interface PermissionPlusMapper extends BaseMapper<PermissionDO> {
    /**
     * Add by qingsheng.chen copy @author mingwei.liu@hand-china.com
     *
     * @param memberId     成员ID
     * @param memberType   成员类型
     * @param permissionCode 权限编码
     * @param sourceType   权限层级
     * @return 租户ID
     */
    List<Long> selectSourceIdsByUserIdAndPermissionEffective(@Param("memberId") long memberId,
                                                             @Param("memberType") String memberType,
                                                             @Param("permissionCode") String permissionCode,
                                                             @Param("sourceType") String sourceType);
}
