package org.apereo.cas.oidc.web.controllers.dynareg;

import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.oidc.dynareg.OidcClientRegistrationRequest;

import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link OidcClientRegistrationRequestTranslatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.6.0
 */
@Tag("OIDC")
public class OidcClientRegistrationRequestTranslatorTests {

    @Nested
    @SuppressWarnings("ClassCanBeStatic")
    @TestPropertySource(properties = "cas.authn.oidc.registration.dynamic-client-registration-mode=OPEN")
    public class OpenRegistrationMode extends AbstractOidcTests {

        @Test
        public void verifyBadLogo() throws Exception {
            val translator = new OidcClientRegistrationRequestTranslator(oidcConfigurationContext);
            val registrationRequest = new OidcClientRegistrationRequest();
            registrationRequest.setRedirectUris(List.of("https://apereo.github.io"));
            registrationRequest.setLogo("https://github.com/apereo.can");
            assertThrows(IllegalArgumentException.class,
                () -> translator.translate(registrationRequest, Optional.empty()));
        }

        @Test
        public void verifyBadPolicy() throws Exception {
            val translator = new OidcClientRegistrationRequestTranslator(oidcConfigurationContext);
            val registrationRequest = new OidcClientRegistrationRequest();
            registrationRequest.setRedirectUris(List.of("https://apereo.github.io"));
            registrationRequest.setPolicyUri("https://github.com/apereo.can");
            assertThrows(IllegalArgumentException.class,
                () -> translator.translate(registrationRequest, Optional.empty()));
        }
    }
}
