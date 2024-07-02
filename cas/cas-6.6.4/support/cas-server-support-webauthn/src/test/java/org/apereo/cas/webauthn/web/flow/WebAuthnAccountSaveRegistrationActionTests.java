package org.apereo.cas.webauthn.web.flow;

import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.EncodingUtils;
import org.apereo.cas.util.RandomUtils;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.config.CasWebflowAccountProfileConfiguration;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.cas.webauthn.storage.WebAuthnCredentialRepository;

import com.yubico.core.SessionManager;
import com.yubico.data.CredentialRegistration;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
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
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link WebAuthnAccountSaveRegistrationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@Tag("WebflowMfaActions")
@Import(CasWebflowAccountProfileConfiguration.class)
@SpringBootTest(classes = BaseWebAuthnWebflowTests.SharedTestConfiguration.class,
    properties = "CasFeatureModule.AccountManagement.enabled=true")
public class WebAuthnAccountSaveRegistrationActionTests {
    @Autowired
    @Qualifier(CasWebflowConstants.ACTION_ID_WEBAUTHN_SAVE_ACCOUNT_REGISTRATION)
    private Action webAuthnSaveAccountRegistrationAction;

    @Autowired
    @Qualifier("webAuthnCredentialRepository")
    private WebAuthnCredentialRepository webAuthnCredentialRepository;

    @Autowired
    @Qualifier("webAuthnSessionManager")
    private SessionManager webAuthnSessionManager;

    @Autowired
    @Qualifier("webAuthnMultifactorAuthenticationProvider")
    private MultifactorAuthenticationProvider webAuthnMultifactorAuthenticationProvider;

    @Test
    public void verifyOperation() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.setParameter("sessionToken", EncodingUtils.encodeBase64(RandomUtils.randomAlphabetic(8)));
        val response = new MockHttpServletResponse();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));
        RequestContextHolder.setRequestContext(context);
        WebUtils.putMultifactorAuthenticationProviderIdIntoFlowScope(context, webAuthnMultifactorAuthenticationProvider);
        ExternalContextHolder.setExternalContext(context.getExternalContext());

        val authn = RegisteredServiceTestUtils.getAuthentication(UUID.randomUUID().toString());
        WebUtils.putAuthentication(authn, context);
        var result = webAuthnSaveAccountRegistrationAction.execute(context);
        assertEquals(CasWebflowConstants.TRANSITION_ID_ERROR, result.getId());

        webAuthnCredentialRepository.addRegistrationByUsername(authn.getPrincipal().getId(),
            CredentialRegistration.builder()
                .credential(RegisteredCredential.builder()
                    .credentialId(ByteArray.fromBase64Url(authn.getPrincipal().getId()))
                    .userHandle(ByteArray.fromBase64Url(RandomUtils.randomAlphabetic(8)))
                    .publicKeyCose(ByteArray.fromBase64Url(RandomUtils.randomAlphabetic(8)))
                    .build())
                .build());
        val token = EncodingUtils.encodeBase64(RandomUtils.randomAlphabetic(8));
        val sessionId = webAuthnSessionManager.createSession(ByteArray.fromBase64(token));
        request.setParameter("sessionToken", sessionId.getBase64Url());

        result = webAuthnSaveAccountRegistrationAction.execute(context);
        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, result.getId());
    }
}
