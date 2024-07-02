package com.alibaba.csp.sentinel.nx.dashboard.rule.nacos.gateway;

import com.alibaba.csp.sentinel.nx.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.nx.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName AuthorityRuleNacosProvider
 * @Description TODO
 * @Author NianXiaoLing
 * @Date 2022/6/15 16:53
 * @Version 1.0
 **/
@Component("gatewayApiDefinitionNacosPublisher")
public class GatewayApiDefinitionNacosPublisher implements DynamicRulePublisher<List<ApiDefinitionEntity>> {
    @Autowired
    private ConfigService configService;


    @Override
    public void publish(String app, List<ApiDefinitionEntity> rules) throws Exception {
        NacosConfigUtil.setRuleStringToNacos(
                this.configService,
                app,
                NacosConfigUtil.GATEWAY_API_DEFINITION_DATA_ID_POSTFIX ,
                rules
        );
    }
}

