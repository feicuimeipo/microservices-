package org.apereo.cas.web;

import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreMultifactorAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreNotificationsConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreTicketsSerializationConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasThymeleafConfiguration;
import org.apereo.cas.config.CoreSamlConfiguration;
import org.apereo.cas.config.DelegatedAuthenticationDynamicDiscoverySelectionConfiguration;
import org.apereo.cas.config.DelegatedAuthenticationWebflowConfiguration;
import org.apereo.cas.config.Pac4jAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.config.Pac4jDelegatedAuthenticationConfiguration;
import org.apereo.cas.config.Pac4jDelegatedAuthenticationSerializationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.pac4j.client.DelegatedClientAuthenticationRequestCustomizer;
import org.apereo.cas.support.pac4j.authentication.clients.DelegatedAuthenticationClientsTestConfiguration;
import org.apereo.cas.util.spring.beans.BeanSupplier;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.DelegatedClientWebflowCustomizer;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasMultifactorAuthenticationWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * This is {@link BaseDelegatedAuthenticationTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
public abstract class BaseDelegatedAuthenticationTests {

    @ImportAutoConfiguration({
        RefreshAutoConfiguration.class,
        ThymeleafAutoConfiguration.class,
        MockMvcAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        AopAutoConfiguration.class
    })
    @SpringBootConfiguration
    @EnableWebMvc
    @Import({
        DelegatedAuthenticationWebflowTestConfiguration.class,
        DelegatedAuthenticationClientsTestConfiguration.class,
        Pac4jDelegatedAuthenticationConfiguration.class,
        Pac4jAuthenticationEventExecutionPlanConfiguration.class,
        Pac4jDelegatedAuthenticationSerializationConfiguration.class,
        DelegatedAuthenticationDynamicDiscoverySelectionConfiguration.class,
        DelegatedAuthenticationWebflowConfiguration.class,

        CasCoreTicketCatalogConfiguration.class,
        CasCoreTicketsConfiguration.class,
        CasCoreTicketIdGeneratorsConfiguration.class,
        CasCoreTicketsSerializationConfiguration.class,
        CasCoreUtilConfiguration.class,
        CasCoreHttpConfiguration.class,
        CoreSamlConfiguration.class,
        CasCoreAuthenticationConfiguration.class,
        CasCoreAuthenticationHandlersConfiguration.class,
        CasCoreAuthenticationPrincipalConfiguration.class,
        CasCoreAuthenticationPolicyConfiguration.class,
        CasCoreAuthenticationMetadataConfiguration.class,
        CasCoreAuthenticationSupportConfiguration.class,
        CasCoreAuthenticationServiceSelectionStrategyConfiguration.class,
        CasCoreNotificationsConfiguration.class,
        CasCoreServicesConfiguration.class,
        CasCoreWebConfiguration.class,
        CasCoreWebflowConfiguration.class,
        CasWebflowContextConfiguration.class,
        CasCoreMultifactorAuthenticationConfiguration.class,
        CasMultifactorAuthenticationWebflowConfiguration.class,
        CasCoreLogoutConfiguration.class,
        CasPersonDirectoryTestConfiguration.class,
        CasCookieConfiguration.class,
        CasThymeleafConfiguration.class,
        CasCoreConfiguration.class,
        CasCoreAuditConfiguration.class,
        CasWebApplicationServiceFactoryConfiguration.class
    })
    public static class SharedTestConfiguration {
    }

    @TestConfiguration(value = "DelegatedAuthenticationWebflowTestConfiguration", proxyBeanMethods = false)
    public static class DelegatedAuthenticationWebflowTestConfiguration {
        @Bean
        public DelegatedClientWebflowCustomizer surrogateCasMultifactorWebflowCustomizer() {
            return BeanSupplier.of(DelegatedClientWebflowCustomizer.class)
                .otherwiseProxy().get();
        }
        
        @Bean
        public DelegatedClientAuthenticationRequestCustomizer testDelegatedClientAuthenticationRequestCustomizer() {
            return BeanSupplier.of(DelegatedClientAuthenticationRequestCustomizer.class)
                .otherwiseProxy()
                .get();
        }
    }
}
