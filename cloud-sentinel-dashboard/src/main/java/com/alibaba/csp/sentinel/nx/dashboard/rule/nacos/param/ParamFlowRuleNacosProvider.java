package com.alibaba.csp.sentinel.nx.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.nx.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ParamFlowRuleNacosProvider
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/15 17:01
 * @Version 1.0
 **/
@Component("paramFlowRuleNacosProvider")
public class ParamFlowRuleNacosProvider implements DynamicRuleProvider<List<ParamFlowRuleEntity>> {
    @Autowired
    private ConfigService configService;

    @Override
    public List<ParamFlowRuleEntity> getRules(String appName) throws Exception {
        return NacosConfigUtil.getRuleEntitiesFromNacos(
                this.configService,
                appName,
                NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX,
                ParamFlowRuleEntity.class
        );
    }
}


