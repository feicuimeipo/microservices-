package com.nx.mybatis.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.KingbaseKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.spi.TenantContextHolder;
import com.nx.mybatis.core.handler.DefaultDBFieldHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Properties;

/**
 * MyBaits 配置类
 *
 * @author 芋道源码
 */
@Slf4j
@Configuration
@MapperScan(value = "${nx.info.base-package}", annotationClass = Mapper.class, lazyInitialization = "${mybatis.lazy-initialization:false}") // Mapper 懒加载，目前仅用于单元测试
public class NxMybatisAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        //租户插件
        mybatisPlusInterceptor.addInnerInterceptor(tenantLineInnerInterceptor());

        //分页插件
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor());

        /*针对 update 和 delete 语句*/
        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        /*针对 update 和 delete 语句*/
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return mybatisPlusInterceptor;
    }


    private TenantLineInnerInterceptor tenantLineInnerInterceptor(){

        return  new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                Long tenantId = CurrentRuntimeContext.getTenantId();
                if (tenantId==null){
                    tenantId = Long.valueOf(CurrentRuntimeContext.DEFAULT_TENANT_ID);
                }
                return new LongValue(tenantId);
            }

            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                if (TenantContextHolder.ignoreTables().isEmpty()){
                    return true;
                }
                for (String prefix : TenantContextHolder.ignoreTables()) {
                    if (tableName.toLowerCase().startsWith(prefix)){
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }
        });

    }

    private PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(-1L);
        paginationInterceptor.setDbType(DbType.MYSQL);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setOptimizeJoin(true);
        return paginationInterceptor;
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler(){
        return new DefaultDBFieldHandler(); // 自动填充参数类
    }

//    /**
//     * 如何有自定义无租户的查询，可以在此过滤
//     * @return SQL解析过滤
//     */
//    @Bean
//    public ISqlParserFilter sqlParserFilter() {
//        return metaObject -> {
//            // 如果在程序中已经手动设置了tenant_id，此处就过滤
//            Object boundSql = metaObject.getValue("boundSql");
//            String sql = String.valueOf(ReflectionUtils.getFieldValue(boundSql, "sql"));
//            return StringUtils.containsIgnoreCase(sql, "insert")
//                    && StringUtils.containsIgnoreCase(sql, "tenant_id");
//        };
//    }

    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "id-type", havingValue = "INPUT")
    public IKeyGenerator keyGenerator(ConfigurableEnvironment environment) {
        DbType dbType = IdTypeEnvironmentPostProcessor.getDbType(environment);
        if (dbType != null) {
            switch (dbType) {
                case POSTGRE_SQL:
                    return new PostgreKeyGenerator();
                case ORACLE:
                case ORACLE_12C:
                    return new OracleKeyGenerator();
                case H2:
                    return new H2KeyGenerator();
                case KINGBASE_ES:
                    return new KingbaseKeyGenerator();
            }
        }
        // 找不到合适的 IKeyGenerator 实现类
        throw new IllegalArgumentException(String.format("DbType{} 找不到合适的 IKeyGenerator 实现类", dbType));
    }



    /**
     * 方言类型识别器
     * @return
     */
    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.put(DbType.ORACLE.getDb(),DbType.ORACLE.getDesc());
        properties.put(DbType.MYSQL.getDb(),DbType.MYSQL.getDesc());
        properties.put(DbType.SQL_SERVER.getDb(),DbType.SQL_SERVER.getDesc());
        properties.put(DbType.POSTGRE_SQL.getDb(),DbType.POSTGRE_SQL.getDesc());
        databaseIdProvider.setProperties(properties);
        //在mapper中标注databaseId="mysql"则表示该sql仅支持MySQL数据库
        return databaseIdProvider;
    }


}
