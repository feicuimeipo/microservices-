package org.apereo.cas.webauthn.web.flow;

import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.RandomUtils;
import org.apereo.cas.web.flow.actions.MultifactorAuthenticationDeviceProviderAction;
import org.apereo.cas.web.flow.config.CasWebflowAccountProfileConfiguration;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.cas.webauthn.storage.WebAuthnCredentialRepository;

import com.yubico.data.CredentialRegistration;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.attestation.Attestation;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.UserIdentity;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link WebAuthnMultifactorDeviceProviderActionTests}.
 *
 * @author Misagh Moayyed
 * @since 6.6.0
 */
@Tag("WebflowMfaActions")
@Import(CasWebflowAccountProfileConfiguration.class)
@SpringBootTest(classes = BaseWebAuthnWebflowTests.SharedTestConfiguration.class,
    properties = "CasFeatureModule.AccountManagement.enabled=true")
public class WebAuthnMultifactorDeviceProviderActionTests {
    @Autowired
    @Qualifier("webAuthnDeviceProviderAction")
    private MultifactorAuthenticationDeviceProviderAction webAuthnDeviceProviderAction;

    @Autowired
    @Qualifier("webAuthnCredentialRepository")
    private WebAuthnCredentialRepository webAuthnCredentialRepository;
    
    @Test
    public void verifyOperation() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));
        RequestContextHolder.setRequestContext(context);
        ExternalContextHolder.setExternalContext(context.getExternalContext());

        val authn = RegisteredServiceTestUtils.getAuthentication(UUID.randomUUID().toString());
        WebUtils.putAuthentication(authn, context);

        webAuthnCredentialRepository.addRegistrationByUsername(authn.getPrincipal().getId(),
            CredentialRegistration.builder()
                .userIdentity(UserIdentity.builder()
                    .name("casuser")
                    .displayName("CAS")
                    .id(ByteArray.fromBase64Url(RandomUtils.randomAlphabetic(8)))
                    .build())
                .credential(RegisteredCredential.builder()
                    .credentialId(ByteArray.fromBase64Url(authn.getPrincipal().getId()))
                    .userHandle(ByteArray.fromBase64Url(RandomUtils.randomAlphabetic(8)))
                    .publicKeyCose(ByteArray.fromBase64Url(RandomUtils.randomAlphabetic(8)))
                    .build())
                .attestationMetadata(Attestation.builder().metadataIdentifier(UUID.randomUUID().toString()).build())
                .registrationTime(Instant.EPOCH)
                .build());

        assertNull(webAuthnDeviceProviderAction.execute(context));
        assertEquals(1, WebUtils.getMultifactorAuthenticationRegisteredDevices(context).size());
    }
}
