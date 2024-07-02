package org.apereo.cas.support.oauth.web.response.accesstoken.ext;

import org.apereo.cas.AbstractOAuth20Tests;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.OAuth20GrantTypes;
import org.apereo.cas.support.oauth.OAuth20ResponseTypes;
import org.apereo.cas.ticket.InvalidTicketException;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jee.context.JEEContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link AccessTokenAuthorizationCodeGrantRequestExtractorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@Tag("OAuth")
public class AccessTokenAuthorizationCodeGrantRequestExtractorTests extends AbstractOAuth20Tests {
    @Autowired
    @Qualifier("accessTokenAuthorizationCodeGrantRequestExtractor")
    private AccessTokenGrantRequestExtractor extractor;

    @Test
    public void verifyNoToken() {
        val request = new MockHttpServletRequest();
        request.addParameter(OAuth20Constants.CLIENT_ID, CLIENT_ID);
        request.addParameter(OAuth20Constants.REDIRECT_URI, REDIRECT_URI);
        request.addParameter(OAuth20Constants.GRANT_TYPE, OAuth20GrantTypes.AUTHORIZATION_CODE.getType());

        val service = getRegisteredService(REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        servicesManager.save(service);

        val response = new MockHttpServletResponse();
        assertEquals(OAuth20ResponseTypes.NONE, extractor.getResponseType());

        val context = new JEEContext(request, response);
        assertThrows(InvalidTicketException.class, () -> extractor.extract(context));
    }

    @Test
    public void verifyDPoPRequest() throws Exception {
        val request = new MockHttpServletRequest();
        request.addParameter(OAuth20Constants.REDIRECT_URI, REDIRECT_URI);
        request.addParameter(OAuth20Constants.GRANT_TYPE, OAuth20GrantTypes.AUTHORIZATION_CODE.getType());
        request.addParameter(OAuth20Constants.CLIENT_ID, CLIENT_ID);
        val service = getRegisteredService(REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        service.setGenerateRefreshToken(true);
        servicesManager.save(service);

        val principal = RegisteredServiceTestUtils.getPrincipal();
        val code = addCode(principal, service);
        ticketRegistry.addTicket(code.getTicketGrantingTicket());
        request.addParameter(OAuth20Constants.CODE, code.getId());

        val response = new MockHttpServletResponse();
        val context = new JEEContext(request, response);

        val profileManager = new ProfileManager(context, oauthDistributedSessionStore);
        profileManager.removeProfiles();

        val commonProfile = new CommonProfile();
        commonProfile.setId(service.getClientId());
        commonProfile.addAttribute(OAuth20Constants.DPOP, "dpop-value");
        commonProfile.addAttribute(OAuth20Constants.DPOP_CONFIRMATION, "dpop-confirmation-value");
        profileManager.save(true, commonProfile, false);

        val result = extractor.extract(context);
        assertNotNull(result);
        assertNotNull(result.getDpop());
        assertNotNull(result.getDpopConfirmation());
    }

    @Test
    public void verifyExtraction() throws Exception {
        val request = new MockHttpServletRequest();
        request.addParameter(OAuth20Constants.REDIRECT_URI, REDIRECT_URI);
        request.addParameter(OAuth20Constants.GRANT_TYPE, OAuth20GrantTypes.AUTHORIZATION_CODE.getType());
        request.addParameter(OAuth20Constants.CLIENT_ID, CLIENT_ID);

        val service = getRegisteredService(REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        service.setGenerateRefreshToken(true);
        servicesManager.save(service);

        val principal = RegisteredServiceTestUtils.getPrincipal();
        val code = addCode(principal, service);
        ticketRegistry.addTicket(code.getTicketGrantingTicket());
        request.addParameter(OAuth20Constants.CODE, code.getId());

        val response = new MockHttpServletResponse();
        val context = new JEEContext(request, response);
        val result = extractor.extract(context);
        assertNotNull(result);
    }

    @Test
    public void verifyExpiredCode() throws Exception {
        val request = new MockHttpServletRequest();
        request.addParameter(OAuth20Constants.REDIRECT_URI, REDIRECT_URI);
        request.addParameter(OAuth20Constants.GRANT_TYPE, OAuth20GrantTypes.AUTHORIZATION_CODE.getType());
        request.addParameter(OAuth20Constants.CLIENT_ID, CLIENT_ID);

        val service = getRegisteredService(REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        service.setGenerateRefreshToken(true);
        servicesManager.save(service);

        val principal = RegisteredServiceTestUtils.getPrincipal();
        val code = addCode(principal, service);
        ticketRegistry.addTicket(code.getTicketGrantingTicket());
        code.markTicketExpired();
        request.addParameter(OAuth20Constants.CODE, code.getId());

        val response = new MockHttpServletResponse();
        val context = new JEEContext(request, response);
        assertThrows(InvalidTicketException.class, () -> extractor.extract(context));
    }

    @Test
    public void verifyExpiredTgt() throws Exception {
        val request = new MockHttpServletRequest();
        request.addParameter(OAuth20Constants.REDIRECT_URI, REDIRECT_URI);
        request.addParameter(OAuth20Constants.GRANT_TYPE, OAuth20GrantTypes.AUTHORIZATION_CODE.getType());
        request.addParameter(OAuth20Constants.CLIENT_ID, CLIENT_ID);

        val service = getRegisteredService(REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        service.setGenerateRefreshToken(true);
        servicesManager.save(service);

        val principal = RegisteredServiceTestUtils.getPrincipal();
        val code = addCode(principal, service);
        code.getTicketGrantingTicket().markTicketExpired();
        ticketRegistry.updateTicket(code);

        request.addParameter(OAuth20Constants.CODE, code.getId());

        val response = new MockHttpServletResponse();
        val context = new JEEContext(request, response);
        assertThrows(InvalidTicketException.class, () -> extractor.extract(context));
    }

    @Test
    public void verifyUnknownService() throws Exception {
        val request = new MockHttpServletRequest();
        request.addParameter(OAuth20Constants.REDIRECT_URI, "unknown.org/abc");
        request.addParameter(OAuth20Constants.GRANT_TYPE, OAuth20GrantTypes.AUTHORIZATION_CODE.getType());
        request.addParameter(OAuth20Constants.CLIENT_ID, "Unknown");

        val service = getRegisteredService(REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        val principal = RegisteredServiceTestUtils.getPrincipal();
        val code = addCode(principal, service);
        ticketRegistry.addTicket(code.getTicketGrantingTicket());
        request.addParameter(OAuth20Constants.CODE, code.getId());

        val response = new MockHttpServletResponse();
        val context = new JEEContext(request, response);
        assertThrows(UnauthorizedServiceException.class, () -> extractor.extract(context));
    }

    @Test
    public void verifyNoClientIdOrRedirectUri() throws Exception {
        val request = new MockHttpServletRequest();
        request.addParameter(OAuth20Constants.GRANT_TYPE, OAuth20GrantTypes.AUTHORIZATION_CODE.getType());

        val service = getRegisteredService(REDIRECT_URI, CLIENT_ID, CLIENT_SECRET);
        val principal = RegisteredServiceTestUtils.getPrincipal();
        val code = addCode(principal, service);
        ticketRegistry.addTicket(code.getTicketGrantingTicket());
        request.addParameter(OAuth20Constants.CODE, code.getId());

        val response = new MockHttpServletResponse();
        val context = new JEEContext(request, response);
        assertThrows(UnauthorizedServiceException.class, () -> extractor.extract(context));
    }

}
