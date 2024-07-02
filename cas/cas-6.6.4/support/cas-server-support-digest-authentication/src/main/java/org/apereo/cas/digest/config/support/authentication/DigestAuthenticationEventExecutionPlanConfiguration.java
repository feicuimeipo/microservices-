package org.apereo.cas.digest.config.support.authentication;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.digest.DigestAuthenticationHandler;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;

import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * This is {@link DigestAuthenticationEventExecutionPlanConfiguration}.
 *
 * @author Misagh Moayyed
 * @author Dmitriy Kopylenko
 * @since 5.1.0
 * @deprecated Since 6.6
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Authentication, module = "digest")
@AutoConfiguration
@Deprecated(since = "6.6")
public class DigestAuthenticationEventExecutionPlanConfiguration {

    @ConditionalOnMissingBean(name = "digestAuthenticationPrincipalFactory")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public PrincipalFactory digestAuthenticationPrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "digestAuthenticationHandler")
    public AuthenticationHandler digestAuthenticationHandler(final CasConfigurationProperties casProperties,
                                                             @Qualifier("digestAuthenticationPrincipalFactory")
                                                             final PrincipalFactory digestAuthenticationPrincipalFactory,
                                                             @Qualifier(ServicesManager.BEAN_NAME)
                                                             final ServicesManager servicesManager) {
        val digest = casProperties.getAuthn().getDigest();
        return new DigestAuthenticationHandler(digest.getName(), servicesManager, digestAuthenticationPrincipalFactory, digest.getOrder());
    }

    @ConditionalOnMissingBean(name = "digestAuthenticationEventExecutionPlanConfigurer")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public AuthenticationEventExecutionPlanConfigurer digestAuthenticationEventExecutionPlanConfigurer(
        @Qualifier("digestAuthenticationHandler")
        final AuthenticationHandler digestAuthenticationHandler,
        @Qualifier(PrincipalResolver.BEAN_NAME_PRINCIPAL_RESOLVER)
        final PrincipalResolver defaultPrincipalResolver) {
        return plan -> plan.registerAuthenticationHandlerWithPrincipalResolver(digestAuthenticationHandler, defaultPrincipalResolver);
    }
}
