package com.alibaba.csp.sentinel.pharmcube.dashboard.rule.nacos.system;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.pharmcube.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName SystemRuleNacosPublisher
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/15 17:03
 * @Version 1.0
 **/
@Component("systemRuleNacosPublisher")
public class SystemRuleNacosPublisher implements DynamicRulePublisher<List<SystemRuleEntity>> {
    @Autowired
    private ConfigService configService;

    @Override
    public void publish(String app, List<SystemRuleEntity> rules) throws Exception {
        NacosConfigUtil.setRuleStringToNacos(
                this.configService,
                app,
                NacosConfigUtil.SYSTEM_DATA_ID_POSTFIX,
                rules
        );
    }
}

