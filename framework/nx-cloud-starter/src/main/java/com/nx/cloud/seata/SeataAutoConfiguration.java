package com.nx.cloud.seata;



import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(
    prefix = "spring.cloud.alibaba.seata",
    name = {"enabled"},
    havingValue = "true"
)
@EnableConfigurationProperties({SeataInitProperties.class})
public class SeataAutoConfiguration {
    private final SeataInitProperties seataInitProperties;

    @Autowired
    public SeataAutoConfiguration(SeataInitProperties seataInitProperties) {
        this.seataInitProperties = seataInitProperties;
    }

    /**
     * init global transaction scanner
     * @Return: GlobalTransactionScanner
     */
    @Bean
    @ConditionalOnMissingBean({GlobalTransactionScanner.class})
    public GlobalTransactionScanner globalTransactionScanner() {
        String appId =  seataInitProperties.getApplicationId();
        String txServiceGroup = seataInitProperties.getTxServiceGroup();
        GlobalTransactionScanner transactionScanner = new GlobalTransactionScanner(appId, txServiceGroup);
        //seataProperties.setEnabled(true);
        return transactionScanner;
    }
}
