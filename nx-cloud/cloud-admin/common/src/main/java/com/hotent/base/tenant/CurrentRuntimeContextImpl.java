package com.hotent.base.tenant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;
import com.pharmcube.api.conf.SpringAppUtils;
import com.pharmcube.api.context.CurrentRuntimeContext;
import org.nianxi.api.feign.AdminServiceApi;
import org.nianxi.utils.JsonUtil;
import org.springframework.util.Assert;

import java.util.Optional;

@AutoService(CurrentRuntimeContext.class)
public class CurrentRuntimeContextImpl implements CurrentRuntimeContext {

    @Override
    public MultiTenantHandler getCurrentTenantHandler(String tenantId) {
        // 租户模式下生成物理表时需要在表名中追加租户别名

        Optional<MultiTenantHandler> handler = tenantHandlers.stream().filter(item->item.getTenantId().equals(tenantId)).findFirst();
        if (handler.isPresent()){
            AdminServiceApi adminServiceApi = SpringAppUtils.getBean(AdminServiceApi.class);
            adminServiceApi

        }

        String currentTenantId = apiContext.getCurrentTenantId();
        // 非平台管理用户
        if(!BootConstant.PLATFORM_TENANT_ID.equals(currentTenantId)) {
            UCApi ucFeign = AppUtil.getBean(UCApi.class);
            JsonNode tenantManage = ucFeign.getTenantById(currentTenantId);
            Assert.notNull(tenantManage, "未获取到当前用户所属的租户信息");
            String tenantCode = JsonUtil.getString((ObjectNode)tenantManage, "code");
            Assert.isTrue(StringUtil.isNotEmpty(tenantCode), "租户中的租户别名为空");
            return tenantCode;
        }

        return null;
    }
}
