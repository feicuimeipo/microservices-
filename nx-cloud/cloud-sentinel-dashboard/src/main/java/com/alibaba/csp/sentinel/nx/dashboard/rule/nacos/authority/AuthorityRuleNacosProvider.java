package com.alibaba.csp.sentinel.nx.dashboard.rule.nacos.authority;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.nx.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.nx.boot.support.AppUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName AuthorityRuleNacosProvider
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/15 16:53
 * @Version 1.0
 **/
@Component("authorityRuleNacosProvider")
public class AuthorityRuleNacosProvider implements DynamicRuleProvider<List<AuthorityRuleEntity>> {


    @Override
    public List<AuthorityRuleEntity> getRules(String appName) throws Exception {
        ConfigService configService = AppUtil.getBean(ConfigService.class);
        return NacosConfigUtil.getRuleEntitiesFromNacos(
                configService,
                appName,
                NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX,
                AuthorityRuleEntity.class
        );

    }
}

