package com.nx.cloud.seata;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(
        prefix = "spring.cloud.alibaba.seata",
        name = {"enabled"},
        havingValue = "true"
)
@Configuration
@ConfigurationProperties(prefix="context.cloud.alibaba.seata")
@ConditionalOnMissingBean(SeataInitProperties.class)
public class SeataInitProperties {
    private String applicationId;
    private String txServiceGroup;
    private boolean enabled;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getTxServiceGroup() {
        return txServiceGroup;
    }

    public void setTxServiceGroup(String txServiceGroup) {
        this.txServiceGroup = txServiceGroup;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
