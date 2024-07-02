package org.apereo.cas.oidc.claims;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.oidc.OidcConstants;
import org.apereo.cas.services.ChainingAttributeReleasePolicy;
import org.apereo.cas.services.RegisteredServiceAttributeReleasePolicyContext;
import org.apereo.cas.services.util.RegisteredServiceJsonSerializer;
import org.apereo.cas.util.CollectionUtils;

import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link OidcPhoneScopeAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("OIDC")
public class OidcPhoneScopeAttributeReleasePolicyTests {
    @Nested
    @SuppressWarnings("ClassCanBeStatic")
    @TestPropertySource(properties = "cas.authn.oidc.core.claims-map.phone_number=cell_phone")
    public class ClaimMappingsTests extends AbstractOidcTests {
        @Test
        public void verifyMappedToUnknown() {
            val policy = new OidcPhoneScopeAttributeReleasePolicy();
            val principal = CoreAuthenticationTestUtils.getPrincipal(CollectionUtils.wrap("phone_number", List.of("12134321245")));
            
            val releasePolicyContext = RegisteredServiceAttributeReleasePolicyContext.builder()
                .registeredService(CoreAuthenticationTestUtils.getRegisteredService())
                .service(CoreAuthenticationTestUtils.getService())
                .principal(principal)
                .build();
            val attrs = policy.getAttributes(releasePolicyContext);
            assertTrue(attrs.containsKey("phone_number"));
        }

        @Test
        public void verifyMapped() {
            val policy = new OidcPhoneScopeAttributeReleasePolicy();
            val principal = CoreAuthenticationTestUtils.getPrincipal(
                CollectionUtils.wrap("cell_phone", List.of("12134321245")));

            val releasePolicyContext = RegisteredServiceAttributeReleasePolicyContext.builder()
                .registeredService(CoreAuthenticationTestUtils.getRegisteredService())
                .service(CoreAuthenticationTestUtils.getService())
                .principal(principal)
                .build();
            val attrs = policy.getAttributes(releasePolicyContext);
            assertTrue(attrs.containsKey("phone_number"));
        }
    }

    @Nested
    @SuppressWarnings("ClassCanBeStatic")
    public class DefaultTests extends AbstractOidcTests {
        @Test
        public void verifyOperation() {
            val policy = new OidcPhoneScopeAttributeReleasePolicy();
            assertEquals(OidcConstants.StandardScopes.PHONE.getScope(), policy.getScopeType());
            assertNotNull(policy.getAllowedAttributes());
            val principal = CoreAuthenticationTestUtils.getPrincipal(CollectionUtils.wrap("phone_number_verified", List.of("12134321245"),
                "phone_number", List.of("12134321245")));
            val releasePolicyContext = RegisteredServiceAttributeReleasePolicyContext.builder()
                .registeredService(CoreAuthenticationTestUtils.getRegisteredService())
                .service(CoreAuthenticationTestUtils.getService())
                .principal(principal)
                .build();
            val attrs = policy.getAttributes(releasePolicyContext);
            assertTrue(policy.getAllowedAttributes().stream().allMatch(attrs::containsKey));
            assertTrue(policy.determineRequestedAttributeDefinitions(releasePolicyContext).containsAll(policy.getAllowedAttributes()));
        }

        @Test
        public void verifySerialization() {
            val appCtx = new StaticApplicationContext();
            appCtx.refresh();
            val policy = new OidcPhoneScopeAttributeReleasePolicy();
            val chain = new ChainingAttributeReleasePolicy();
            chain.addPolicies(policy);
            val service = getOidcRegisteredService();
            service.setAttributeReleasePolicy(chain);
            val serializer = new RegisteredServiceJsonSerializer(appCtx);
            val json = serializer.toString(service);
            assertNotNull(json);
            assertNotNull(serializer.from(json));
        }
    }
}
