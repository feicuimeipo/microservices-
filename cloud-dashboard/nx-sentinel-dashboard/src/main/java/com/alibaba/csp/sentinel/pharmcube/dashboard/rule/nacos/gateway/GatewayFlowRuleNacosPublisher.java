package com.alibaba.csp.sentinel.pharmcube.dashboard.rule.nacos.gateway;

import com.alibaba.csp.sentinel.pharmcube.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
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
@Component("gatewayFlowRuleNacosPublisher")
public class GatewayFlowRuleNacosPublisher implements DynamicRulePublisher<List<GatewayFlowRuleEntity>> {
    @Autowired
    private ConfigService configService;


    @Override
    public void publish(String app, List<GatewayFlowRuleEntity> rules) throws Exception {
        NacosConfigUtil.setRuleStringToNacos(
                this.configService,
                app,
                NacosConfigUtil.GATEWAY_FLOW_DATA_ID_POSTFIX ,
                rules
        );
    }

}

