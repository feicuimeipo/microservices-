package org.apereo.cas.web.flow;

import org.apereo.cas.api.PasswordlessTokenRepository;
import org.apereo.cas.api.PasswordlessUserAccount;
import org.apereo.cas.web.support.WebUtils;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockFlowExecutionContext;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link AcceptPasswordlessAuthenticationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Import(BaseWebflowConfigurerTests.SharedTestConfiguration.class)
@Tag("WebflowAuthenticationActions")
@TestPropertySource(properties = "cas.authn.passwordless.accounts.simple.casuser=casuser@example.org")
public class AcceptPasswordlessAuthenticationActionTests extends BasePasswordlessAuthenticationActionTests {
    @Autowired
    @Qualifier(CasWebflowConstants.ACTION_ID_ACCEPT_PASSWORDLESS_AUTHN)
    private Action acceptPasswordlessAuthenticationAction;

    @Autowired
    @Qualifier(PasswordlessTokenRepository.BEAN_NAME)
    private PasswordlessTokenRepository passwordlessTokenRepository;

    @Test
    public void verifyAction() throws Exception {
        val exec = new MockFlowExecutionContext(new MockFlowSession(new Flow(CasWebflowConfigurer.FLOW_ID_LOGIN)));
        val context = new MockRequestContext(exec);

        val account = PasswordlessUserAccount.builder()
            .email("email")
            .phone("phone")
            .username("casuser")
            .name("casuser")
            .build();
        WebUtils.putPasswordlessAuthenticationAccount(context, account);

        val token = passwordlessTokenRepository.createToken("casuser");

        val request = new MockHttpServletRequest();
        request.addParameter("token", token);

        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        assertEquals(CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, acceptPasswordlessAuthenticationAction.execute(context).getId());

        passwordlessTokenRepository.saveToken("casuser", token);

        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, acceptPasswordlessAuthenticationAction.execute(context).getId());
        assertTrue(passwordlessTokenRepository.findToken("casuser").isEmpty());
    }

    @Test
    public void verifyMissingTokenAction() throws Exception {
        val exec = new MockFlowExecutionContext(new MockFlowSession(new Flow(CasWebflowConfigurer.FLOW_ID_LOGIN)));
        val context = new MockRequestContext(exec);
        val request = new MockHttpServletRequest();

        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val account = PasswordlessUserAccount.builder()
            .email("email")
            .phone("phone")
            .username("casuser")
            .name("casuser")
            .build();
        WebUtils.putPasswordlessAuthenticationAccount(context, account);
        assertEquals(CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, acceptPasswordlessAuthenticationAction.execute(context).getId());
    }
}
