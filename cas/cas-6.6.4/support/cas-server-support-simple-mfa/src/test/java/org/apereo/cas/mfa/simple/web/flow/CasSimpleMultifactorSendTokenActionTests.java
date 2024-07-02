package org.apereo.cas.mfa.simple.web.flow;

import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.mfa.simple.BaseCasSimpleMultifactorAuthenticationTests;
import org.apereo.cas.mfa.simple.CasSimpleMultifactorTokenCredential;
import org.apereo.cas.util.junit.EnabledIfListeningOnPort;
import org.apereo.cas.web.flow.CasWebflowConstants;

import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import javax.security.auth.login.FailedLoginException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link CasSimpleMultifactorSendTokenActionTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@EnabledIfListeningOnPort(port = 25000)
@Tag("Mail")
public class CasSimpleMultifactorSendTokenActionTests {
    @SuppressWarnings("ClassCanBeStatic")
    @Nested
    @TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=25000",

        "cas.authn.mfa.simple.mail.from=admin@example.org",
        "cas.authn.mfa.simple.mail.subject=CAS Token",
        "cas.authn.mfa.simple.mail.text=CAS Token is ${token}",

        "cas.authn.mfa.simple.sms.from=347746512"
    })
    @Import(BaseCasSimpleMultifactorAuthenticationTests.CasSimpleMultifactorTestConfiguration.class)
    public class DefaultCasSimpleMultifactorSendTokenActionTests extends BaseCasSimpleMultifactorSendTokenActionTests {
        @Test
        public void verifyOperation() throws Exception {
            val theToken = createToken("casuser").getKey();
            assertNotNull(this.ticketRegistry.getTicket(theToken));
            val token = new CasSimpleMultifactorTokenCredential(theToken);
            val result = authenticationHandler.authenticate(token, mock(Service.class));
            assertNotNull(result);
            assertNull(this.ticketRegistry.getTicket(theToken));
        }

        @Test
        public void verifyReusingExistingTokens() throws Exception {
            val pair = createToken("casuser");

            val theToken = pair.getKey();
            assertNotNull(this.ticketRegistry.getTicket(theToken));

            val event = mfaSimpleMultifactorSendTokenAction.execute(pair.getValue());
            assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, event.getId());

            val token = new CasSimpleMultifactorTokenCredential(theToken);
            val result = authenticationHandler.authenticate(token, mock(Service.class));
            assertNotNull(result);
            assertNull(this.ticketRegistry.getTicket(theToken));
        }

        @Test
        public void verifyFailsForUser() throws Exception {
            val theToken1 = createToken("casuser1");
            assertNotNull(theToken1);

            val theToken2 = createToken("casuser2");
            assertNotNull(theToken2);
            val token = new CasSimpleMultifactorTokenCredential(theToken1.getKey());
            assertThrows(FailedLoginException.class, () -> authenticationHandler.authenticate(token, mock(Service.class)));

        }
    }

    @SuppressWarnings("ClassCanBeStatic")
    @Nested
    public class NoCommunicationStrategyTests extends BaseCasSimpleMultifactorSendTokenActionTests {
        @Test
        public void verifyOperation() throws Exception {
            val context = buildRequestContextFor("casuser");
            val event = mfaSimpleMultifactorSendTokenAction.execute(context);
            assertEquals(CasWebflowConstants.TRANSITION_ID_ERROR, event.getId());
        }
    }
}
