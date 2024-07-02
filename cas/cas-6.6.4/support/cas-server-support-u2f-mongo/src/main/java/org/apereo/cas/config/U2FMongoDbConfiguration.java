package org.apereo.cas.config;

import org.apereo.cas.adaptors.u2f.storage.U2FDeviceRepository;
import org.apereo.cas.adaptors.u2f.storage.U2FMongoDbDeviceRepository;
import org.apereo.cas.authentication.CasSSLContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.mongo.MongoDbConnectionFactory;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * This is {@link U2FMongoDbConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.U2F, module = "mongo")
@AutoConfiguration
public class U2FMongoDbConfiguration {

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public U2FDeviceRepository u2fDeviceRepository(
        final CasConfigurationProperties casProperties,
        @Qualifier("u2fRegistrationRecordCipherExecutor")
        final CipherExecutor u2fRegistrationRecordCipherExecutor,
        @Qualifier(CasSSLContext.BEAN_NAME)
        final CasSSLContext casSslContext) {
        val u2f = casProperties.getAuthn().getMfa().getU2f();
        val factory = new MongoDbConnectionFactory(casSslContext.getSslContext());
        val mongoProps = u2f.getMongo();
        val mongoTemplate = factory.buildMongoTemplate(mongoProps);
        MongoDbConnectionFactory.createCollection(mongoTemplate, mongoProps.getCollection(), mongoProps.isDropCollection());
        final LoadingCache<String, String> requestStorage =
            Caffeine.newBuilder().expireAfterWrite(u2f.getCore().getExpireRegistrations(), u2f.getCore().getExpireRegistrationsTimeUnit()).build(key -> StringUtils.EMPTY);
        return new U2FMongoDbDeviceRepository(requestStorage, mongoTemplate,
            u2fRegistrationRecordCipherExecutor, casProperties);
    }
}
