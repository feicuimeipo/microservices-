package com.alibaba.csp.sentinel.pharmcube.dashboard.rule.nacos.gateway;

import com.alibaba.csp.sentinel.pharmcube.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.pharmcube.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * @ClassName AuthorityRuleNacosProvider
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/15 16:53
 * @Version 1.0
 **/
@Component("gatewayApiDefinitionNacosProvider")
public class GatewayApiDefinitionNacosProvider implements DynamicRuleProvider<List<ApiDefinitionEntity>> {
    @Autowired
    private ConfigService configService;
    @Override
    public List<ApiDefinitionEntity> getRules(String appName) throws Exception {
        return NacosConfigUtil.getRuleEntitiesFromNacos(
                this.configService,
                appName,
                NacosConfigUtil.GATEWAY_API_DEFINITION_DATA_ID_POSTFIX ,
                ApiDefinitionEntity.class
        );

    }
}

