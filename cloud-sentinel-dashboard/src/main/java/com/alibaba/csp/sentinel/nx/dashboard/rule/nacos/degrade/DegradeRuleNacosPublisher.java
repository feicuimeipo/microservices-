package com.alibaba.csp.sentinel.nx.dashboard.rule.nacos.degrade;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.nx.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @ClassName DegradeRuleNacosPublisher
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/15 16:57
 * @Version 1.0
 **/
@Component("degradeRuleNacosPublisher")
public class DegradeRuleNacosPublisher implements DynamicRulePublisher<List<DegradeRuleEntity>> {
    @Autowired
    private ConfigService configService;

    @Override
    public void publish(String app, List<DegradeRuleEntity> rules) throws Exception {
        NacosConfigUtil.setRuleStringToNacos(
                this.configService,
                app,
                NacosConfigUtil.DEGRADE_DATA_ID_POSTFIX,
                rules
        );
    }
}

