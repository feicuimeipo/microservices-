package com.nx.storage.config;


import com.nx.common.banner.BannerUtils;
import com.nx.storage.core.client.FileClientFactory;
import com.nx.storage.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author 念熹科技
 */
@Configuration
public class NxStorageAutoConfiguration {

    public NxStorageAutoConfiguration(){
        BannerUtils.push(this.getClass(),new String[]{"nx-sto-adapter enabled"});
    }

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
