package org.apereo.cas.web.flow.delegation;

import org.apereo.cas.api.PasswordlessUserAccount;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.BasePasswordlessAuthenticationActionTests;
import org.apereo.cas.web.flow.BaseWebflowConfigurerTests;
import org.apereo.cas.web.flow.DelegatedClientIdentityProviderAuthorizer;
import org.apereo.cas.web.support.WebUtils;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.webflow.test.MockRequestContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link PasswordlessDelegatedClientIdentityProviderAuthorizerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.6.0
 */
@Import(BaseWebflowConfigurerTests.SharedTestConfiguration.class)
@Tag("WebflowAuthenticationActions")
public class PasswordlessDelegatedClientIdentityProviderAuthorizerTests extends BasePasswordlessAuthenticationActionTests {
    @Autowired
    @Qualifier("passwordlessDelegatedClientIdentityProviderAuthorizer")
    private DelegatedClientIdentityProviderAuthorizer authorizer;

    @Autowired
    @Qualifier(ServicesManager.BEAN_NAME)
    private ServicesManager servicesManager;

    @Test
    public void verifyNoneDefined() throws Exception {
        val context = new MockRequestContext();
        val account = PasswordlessUserAccount.builder()
            .username("casuser")
            .allowedDelegatedClients(List.of())
            .build();
        WebUtils.putPasswordlessAuthenticationAccount(context, account);
        assertTrue(isAuthorized(context));
    }

    @Test
    public void verifyDefined() throws Exception {
        val context = new MockRequestContext();
        val account = PasswordlessUserAccount.builder()
            .username("casuser")
            .allowedDelegatedClients(List.of("CasClient"))
            .build();
        WebUtils.putPasswordlessAuthenticationAccount(context, account);
        assertTrue(isAuthorized(context));
    }

    @Test
    public void verifyUnknown() throws Exception {
        val context = new MockRequestContext();
        val account = PasswordlessUserAccount.builder()
            .username("casuser")
            .allowedDelegatedClients(List.of("AnotherClient"))
            .build();
        WebUtils.putPasswordlessAuthenticationAccount(context, account);
        assertFalse(isAuthorized(context));
    }

    @Test
    public void verifyNoAccount() throws Exception {
        val context = new MockRequestContext();
        assertFalse(isAuthorized(context));
    }


    private boolean isAuthorized(final MockRequestContext context) {
        val service = RegisteredServiceTestUtils.getService(UUID.randomUUID().toString());
        val registeredService = RegisteredServiceTestUtils.getRegisteredService(service.getId());
        servicesManager.save(registeredService);
        return authorizer.isDelegatedClientAuthorizedFor("CasClient", service, context);
    }
}
