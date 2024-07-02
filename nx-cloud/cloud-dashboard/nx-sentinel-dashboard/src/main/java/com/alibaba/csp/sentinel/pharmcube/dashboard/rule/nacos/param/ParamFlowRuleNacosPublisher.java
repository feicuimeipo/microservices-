package com.alibaba.csp.sentinel.pharmcube.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.pharmcube.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ParamFlowRuleNacosPublisher
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/15 17:02
 * @Version 1.0
 **/
@Component("paramFlowRuleNacosPublisher")
public class ParamFlowRuleNacosPublisher implements DynamicRulePublisher<List<ParamFlowRuleEntity>> {
    @Autowired
    private ConfigService configService;

    @Override
    public void publish(String app, List<ParamFlowRuleEntity> rules) throws Exception {
        NacosConfigUtil.setRuleStringToNacos(
                this.configService,
                app,
                NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX,
                rules
        );
    }
}

