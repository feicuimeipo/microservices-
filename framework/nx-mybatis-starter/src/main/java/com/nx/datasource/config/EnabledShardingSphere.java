package com.nx.datasource.config;


import com.nx.datasource.core.shardingsphere.NxShardingSphereDynamicConfiguration;
import org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({NxShardingSphereDynamicConfiguration.class, SpringBootConfiguration.class})
public @interface EnabledShardingSphere {
}
