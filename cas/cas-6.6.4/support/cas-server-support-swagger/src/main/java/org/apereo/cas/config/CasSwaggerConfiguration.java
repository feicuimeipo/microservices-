package org.apereo.cas.config;

import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.util.CasVersion;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * This is {@link CasSwaggerConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Discovery)
@AutoConfiguration
public class CasSwaggerConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "casSwaggerOpenApi")
    public OpenAPI casSwaggerOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Apereo CAS Swagger API Documentation")
                .description("Apereo CAS Swagger API Documentation")
                .version(CasVersion.asString())
                .license(new License().name("Apache 2.0")
                    .url("https://github.com/apereo/cas/blob/master/LICENSE")))
            .externalDocs(new ExternalDocumentation()
                .description("Apereo CAS Documentation")
                .url("https://apereo.github.com/cas"));
    }
}
