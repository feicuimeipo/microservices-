package com.nx.boot.tenant;

import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.constant.NxRequestHeaders;

/**
 * 多租户 Util
 *
 * @author 芋道源码
 */
public class TenantIgnoreUtils {

    /**
     * 使用指定租户，执行对应的逻辑
     *
     * 注意，如果当前是忽略租户的情况下，会被强制设置成不忽略租户
     * 当然，执行完成后，还是会恢复回去
     *
     * @param tenantId 租户编号
     * @param runnable 逻辑
     */
    public static void execute(Long tenantId, Runnable runnable) {
        Long oldTenantId = CurrentRuntimeContext.getTenantId();
        boolean oldIgnore = TenantIgnoreUtils.getIgnoreTenantId();
        try {
            CurrentRuntimeContext.setTenantId(tenantId);
            setIgnoreTenantId(false);
            // 执行逻辑
            runnable.run();
        } finally {
            CurrentRuntimeContext.setTenantId(oldTenantId);
            setIgnoreTenantId(oldIgnore);
        }
    }

    public static void setIgnoreTenantId(Boolean value){
        CurrentRuntimeContext.addContextHeader(NxRequestHeaders.HEADER_IGNORE_TENANT_ID,value.toString());
    }


    public static boolean getIgnoreTenantId(){
        String oldIgnore = CurrentRuntimeContext.getContextHeader(NxRequestHeaders.HEADER_IGNORE_TENANT_ID);
        if (oldIgnore==null || !oldIgnore.equalsIgnoreCase("true")){
            oldIgnore = "false";
        }
        return Boolean.valueOf(oldIgnore);
    }

}
