package com.nx.mybatis.core.dataobject;

import lombok.Data;


/**
 * import com.nx.ds.mybatis.core.dataobject.TenantBaseDO;
 */
@Data
public abstract class TenantBaseDO extends BaseDO {
    private Long tenantId;
}
