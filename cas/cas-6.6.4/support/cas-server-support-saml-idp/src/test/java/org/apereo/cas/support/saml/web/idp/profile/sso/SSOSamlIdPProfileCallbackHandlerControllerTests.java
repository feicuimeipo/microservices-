package org.apereo.cas.support.saml.web.idp.profile.sso;

import org.apereo.cas.CasProtocolConstants;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.SamlProtocolConstants;
import org.apereo.cas.support.saml.SamlUtils;
import org.apereo.cas.support.saml.authentication.SamlIdPAuthenticationContext;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.ServiceTicketSessionTrackingPolicy;
import org.apereo.cas.util.EncodingUtils;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link SSOSamlIdPProfileCallbackHandlerControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Tag("SAML2Web")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = "cas.authn.saml-idp.metadata.file-system.location=file:src/test/resources/metadata")
public class SSOSamlIdPProfileCallbackHandlerControllerTests extends BaseSamlIdPConfigurationTests {
    @Autowired
    @Qualifier("ssoPostProfileCallbackHandlerController")
    private SSOSamlIdPProfileCallbackHandlerController controller;

    private SamlRegisteredService samlRegisteredService;

    @BeforeEach
    public void beforeEach() {
        samlRegisteredService = getSamlRegisteredServiceFor(false, false,
            false, "https://cassp.example.org");
        servicesManager.save(samlRegisteredService);
    }

    @Test
    public void verifyNoRequest() {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        assertThrows(IllegalArgumentException.class, () -> controller.handleCallbackProfileRequestGet(response, request));
    }

    @Test
    public void verifyNoTicket() throws Exception {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();

        val authnRequest = signAuthnRequest(request, response, getAuthnRequest());
        val xml = SamlUtils.transformSamlObject(openSamlConfigBean, authnRequest).toString();
        request.getSession().setAttribute(SamlProtocolConstants.PARAMETER_SAML_REQUEST, EncodingUtils.encodeBase64(xml));
        val context = new MessageContext();
        context.setMessage(authnRequest);
        request.getSession().setAttribute(MessageContext.class.getName(), SamlIdPAuthenticationContext.from(context).encode());
        val mv = controller.handleCallbackProfileRequestGet(response, request);
        assertEquals(HttpStatus.BAD_REQUEST, mv.getStatus());
    }

    @Test
    @Order(1)
    public void verifyValidationByPost() throws Exception {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();

        val authn = getAuthnRequest();
        authn.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        val authnRequest = signAuthnRequest(request, response, authn);
        val context = new MessageContext();
        context.setMessage(authnRequest);
        val xml = SamlUtils.transformSamlObject(openSamlConfigBean, authnRequest).toString();
        request.getSession().setAttribute(SamlProtocolConstants.PARAMETER_SAML_REQUEST, EncodingUtils.encodeBase64(xml));
        request.getSession().setAttribute(SamlProtocolConstants.PARAMETER_SAML_RELAY_STATE, UUID.randomUUID().toString());
        request.getSession().setAttribute(MessageContext.class.getName(), SamlIdPAuthenticationContext.from(context).encode());
        val st1 = getServiceTicket();
        request.addParameter(CasProtocolConstants.PARAMETER_TICKET, st1.getId());
        controller.handleCallbackProfileRequestGet(response, request);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    private ServiceTicket getServiceTicket() throws Exception {
        val tgt = new MockTicketGrantingTicket(UUID.randomUUID().toString());
        ticketRegistry.addTicket(tgt);
        val trackingPolicy = mock(ServiceTicketSessionTrackingPolicy.class);
        val ticketService = RegisteredServiceTestUtils.getService(samlRegisteredService.getServiceId());
        ticketService.getAttributes().put(SamlProtocolConstants.PARAMETER_ENTITY_ID, List.of(samlRegisteredService.getServiceId()));
        val st1 = tgt.grantServiceTicket(ticketService, trackingPolicy);
        ticketRegistry.addTicket(st1);
        ticketRegistry.updateTicket(tgt);
        return st1;
    }

    @Test
    @Order(2)
    public void verifyValidationByRedirect() throws Exception {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();

        val authn = getAuthnRequest();
        authn.setProtocolBinding(SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI);
        val authnRequest = signAuthnRequest(request, response, authn);
        val xml = SamlUtils.transformSamlObject(openSamlConfigBean, authnRequest).toString();
        request.getSession().setAttribute(SamlProtocolConstants.PARAMETER_SAML_REQUEST, EncodingUtils.encodeBase64(xml));
        request.getSession().setAttribute(SamlProtocolConstants.PARAMETER_SAML_RELAY_STATE, UUID.randomUUID().toString());
        val context = new MessageContext();
        context.setMessage(authnRequest);
        request.getSession().setAttribute(MessageContext.class.getName(), SamlIdPAuthenticationContext.from(context).encode());
        val st1 = getServiceTicket();
        request.addParameter(CasProtocolConstants.PARAMETER_TICKET, st1.getId());
        controller.handleCallbackProfileRequestGet(response, request);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    private AuthnRequest signAuthnRequest(final HttpServletRequest request,
                                          final HttpServletResponse response,
                                          final AuthnRequest authnRequest) throws Exception {
        val adaptor = SamlRegisteredServiceServiceProviderMetadataFacade
            .get(samlRegisteredServiceCachingMetadataResolver, samlRegisteredService,
                samlRegisteredService.getServiceId()).get();
        return samlIdPObjectSigner.encode(authnRequest, samlRegisteredService,
            adaptor, response, request, SAMLConstants.SAML2_POST_BINDING_URI, authnRequest, new MessageContext());
    }

    private AuthnRequest getAuthnRequest() {
        var builder = (SAMLObjectBuilder) openSamlConfigBean.getBuilderFactory()
            .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        var authnRequest = (AuthnRequest) builder.buildObject();
        builder = (SAMLObjectBuilder) openSamlConfigBean.getBuilderFactory()
            .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        val issuer = (Issuer) builder.buildObject();
        issuer.setValue(samlRegisteredService.getServiceId());
        authnRequest.setIssuer(issuer);
        return authnRequest;
    }
}
