package org.apereo.cas.pm.web.flow.actions;

import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.expiration.HardTimeoutExpirationPolicy;
import org.apereo.cas.ticket.expiration.MultiTimeUseOrTimeoutExpirationPolicy;
import org.apereo.cas.util.HttpRequestUtils;
import org.apereo.cas.util.junit.EnabledIfListeningOnPort;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.support.WebUtils;

import lombok.val;
import org.apereo.inspektr.common.web.ClientInfo;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link SendPasswordResetInstructionsActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@EnabledIfListeningOnPort(port = 25000)
@Tag("Mail")
public class SendPasswordResetInstructionsActionTests {

    @TestConfiguration(value = "PasswordManagementTestConfiguration", proxyBeanMethods = false)
    public static class PasswordManagementTestConfiguration {
        @Bean
        @Autowired
        public PasswordManagementService passwordChangeService() {
            val service = mock(PasswordManagementService.class);
            when(service.createToken(any())).thenReturn(null);
            when(service.findUsername(any())).thenReturn("casuser");
            when(service.findEmail(any())).thenReturn("casuser@example.org");
            return service;
        }
    }

    @SuppressWarnings("ClassCanBeStatic")
    @Nested
    public class DefaultTests extends BasePasswordManagementActionTests {

        @BeforeEach
        public void setup() {
            val request = new MockHttpServletRequest();
            request.setRemoteAddr("223.456.789.000");
            request.setLocalAddr("123.456.789.000");
            request.addHeader(HttpRequestUtils.USER_AGENT_HEADER, "test");
            ClientInfoHolder.setClientInfo(new ClientInfo(request));
            ticketRegistry.deleteAll();
        }

        @Test
        public void verifyAction() throws Exception {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            request.addParameter("username", "casuser");
            WebUtils.putServiceIntoFlowScope(context, RegisteredServiceTestUtils.getService());
            context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, sendPasswordResetInstructionsAction.execute(context).getId());
            val tickets = ticketRegistry.getTickets();
            assertEquals(1, tickets.size());
            assertInstanceOf(HardTimeoutExpirationPolicy.class, tickets.iterator().next().getExpirationPolicy());
        }

        @Test
        public void verifyNoPhoneOrEmail() throws Exception {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            request.addParameter("username", "none");
            WebUtils.putServiceIntoFlowScope(context, RegisteredServiceTestUtils.getService());
            context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            assertEquals(CasWebflowConstants.TRANSITION_ID_ERROR, sendPasswordResetInstructionsAction.execute(context).getId());
        }

        @Test
        public void verifyNoUsername() throws Exception {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            WebUtils.putServiceIntoFlowScope(context, RegisteredServiceTestUtils.getService());
            context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            assertEquals(CasWebflowConstants.TRANSITION_ID_ERROR, sendPasswordResetInstructionsAction.execute(context).getId());
        }
    }

    @SuppressWarnings("ClassCanBeStatic")
    @Nested
    @SpringBootTest(classes = {
            BasePasswordManagementActionTests.SharedTestConfiguration.class,
            CasPersonDirectoryTestConfiguration.class
    }, properties = {
            "spring.mail.host=localhost",
            "spring.mail.port=25000",

            "cas.authn.pm.core.enabled=true",
            "cas.authn.pm.groovy.location=classpath:PasswordManagementService.groovy",
            "cas.authn.pm.forgot-username.mail.from=cas@example.org",
            "cas.authn.pm.reset.mail.from=cas@example.org",
            "cas.authn.pm.reset.security-questions-enabled=true",
            "cas.authn.pm.reset.number-of-uses=1"
    })
    public class MultiUseTests extends BasePasswordManagementActionTests {

        @Test
        public void verifyActionMultiUse() throws Exception {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            request.addParameter("username", "casuser");
            WebUtils.putServiceIntoFlowScope(context, RegisteredServiceTestUtils.getService());
            context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, sendPasswordResetInstructionsAction.execute(context).getId());
            val tickets = ticketRegistry.getTickets();
            assertEquals(1, tickets.size());
            assertInstanceOf(MultiTimeUseOrTimeoutExpirationPolicy.class, tickets.iterator().next().getExpirationPolicy());
        }
    }

    @SuppressWarnings("ClassCanBeStatic")
    @Nested
    @Import(PasswordManagementTestConfiguration.class)
    public class WithoutTokens extends BasePasswordManagementActionTests {

        @Test
        public void verifyNoLinkAction() throws Exception {
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            request.addParameter("username", "unknown");
            WebUtils.putServiceIntoFlowScope(context, RegisteredServiceTestUtils.getService());
            context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
            assertEquals(CasWebflowConstants.TRANSITION_ID_ERROR, sendPasswordResetInstructionsAction.execute(context).getId());
        }
    }
}
