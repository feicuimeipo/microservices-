package org.apereo.cas.services;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.entity.SamlIdentityProviderEntity;
import org.apereo.cas.entity.SamlIdentityProviderEntityParser;
import org.apereo.cas.web.DelegatedClientIdentityProviderConfiguration;
import org.apereo.cas.web.DelegatedClientIdentityProviderConfigurationFactory;
import org.apereo.cas.web.flow.DelegatedClientIdentityProviderAuthorizer;
import org.apereo.cas.web.support.ArgumentExtractor;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Clients;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.saml.client.SAML2Client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is {@link DefaultSamlIdentityProviderDiscoveryFeedService}.
 *
 * @author Sam Hough
 * @since 6.6.0
 */
@RequiredArgsConstructor
public class DefaultSamlIdentityProviderDiscoveryFeedService implements SamlIdentityProviderDiscoveryFeedService {

    private final CasConfigurationProperties casProperties;

    private final List<SamlIdentityProviderEntityParser> parsers;

    private final Clients clients;

    private final ArgumentExtractor argumentExtractor;

    private final List<DelegatedClientIdentityProviderAuthorizer> authorizers;

    @Override
    public Collection<SamlIdentityProviderEntity> getDiscoveryFeed() {
        return parsers
            .stream()
            .map(SamlIdentityProviderEntityParser::getIdentityProviders)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getEntityIds() {
        return clients.findAllClients()
            .stream()
            .filter(c -> c instanceof SAML2Client)
            .map(SAML2Client.class::cast)
            .peek(InitializableObject::init)
            .map(SAML2Client::getServiceProviderResolvedEntityId)
            .collect(Collectors.toList());
    }

    @Override
    public DelegatedClientIdentityProviderConfiguration getProvider(final String entityID,
                                                                    final HttpServletRequest httpServletRequest,
                                                                    final HttpServletResponse httpServletResponse) {
        val idp = getDiscoveryFeed()
            .stream()
            .filter(entity -> entity.getEntityID().equals(entityID))
            .findFirst()
            .orElseThrow();
        val samlClient = clients.findAllClients()
            .stream()
            .filter(c -> c instanceof SAML2Client)
            .map(SAML2Client.class::cast)
            .peek(InitializableObject::init)
            .filter(c -> c.getIdentityProviderResolvedEntityId().equalsIgnoreCase(idp.getEntityID()))
            .findFirst()
            .orElseThrow();

        val webContext = new JEEContext(httpServletRequest, httpServletResponse);
        val service = argumentExtractor.extractService(httpServletRequest);

        val authorized = authorizers
            .stream()
            .allMatch(authz -> authz.isDelegatedClientAuthorizedForService(samlClient, service, httpServletRequest));

        if (authorized) {
            val provider = DelegatedClientIdentityProviderConfigurationFactory.builder()
                .service(service)
                .client(samlClient)
                .webContext(webContext)
                .casProperties(casProperties)
                .build()
                .resolve();

            if (provider.isPresent()) {
                return provider.get();
            }
        }
        throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, StringUtils.EMPTY);
    }
}
