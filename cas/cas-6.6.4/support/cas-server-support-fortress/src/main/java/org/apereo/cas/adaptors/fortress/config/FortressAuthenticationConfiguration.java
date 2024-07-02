package org.apereo.cas.adaptors.fortress.config;

import org.apereo.cas.adaptors.fortress.FortressAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.rest.AccessMgrRestImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * This is {@link FortressAuthenticationConfiguration}.
 *
 * @author yudhi.k.surtan
 * @since 5.2.0
 * @deprecated Since 6.6.
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Authentication, module = "fortress")
@AutoConfiguration
@Deprecated(since = "6.6")
public class FortressAuthenticationConfiguration {

    @ConditionalOnMissingBean(name = "fortressPrincipalFactory")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public PrincipalFactory fortressPrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "fortressAccessManager")
    public AccessMgr fortressAccessManager(final CasConfigurationProperties casProperties) {
        val rbacContext = casProperties.getAuthn().getFortress().getRbaccontext();
        LOGGER.trace("Registering fortress access manager with context: [{}]", rbacContext);
        val accessMgrRestImpl = new AccessMgrRestImpl();
        accessMgrRestImpl.setContextId(casProperties.getAuthn().getFortress().getRbaccontext());
        return accessMgrRestImpl;
    }

    @ConditionalOnMissingBean(name = "fortressAuthenticationHandler")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public AuthenticationHandler fortressAuthenticationHandler(
        @Qualifier("fortressAccessManager")
        final AccessMgr fortressAccessManager,
        @Qualifier("fortressPrincipalFactory")
        final PrincipalFactory fortressPrincipalFactory,
        @Qualifier(ServicesManager.BEAN_NAME)
        final ServicesManager servicesManager) {
        return new FortressAuthenticationHandler(fortressAccessManager, null, servicesManager, fortressPrincipalFactory, null);
    }

    @ConditionalOnMissingBean(name = "fortressAuthenticationEventExecutionPlanConfigurer")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public AuthenticationEventExecutionPlanConfigurer fortressAuthenticationEventExecutionPlanConfigurer(
        @Qualifier("fortressAuthenticationHandler")
        final AuthenticationHandler fortressAuthenticationHandler) {
        return plan -> {
            LOGGER.debug("Registering fortress authentication event execution plan");
            plan.registerAuthenticationHandler(fortressAuthenticationHandler);
        };
    }
}
