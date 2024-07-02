package org.apereo.cas.oidc.discovery;

import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.authentication.MultifactorAuthenticationUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.oidc.issuer.OidcIssuerService;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is {@link OidcServerDiscoverySettingsFactory}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@RequiredArgsConstructor
public class OidcServerDiscoverySettingsFactory implements FactoryBean<OidcServerDiscoverySettings> {
    private final CasConfigurationProperties casProperties;

    private final OidcIssuerService issuerService;

    private final ConfigurableApplicationContext applicationContext;

    @Override
    public OidcServerDiscoverySettings getObject() {
        val oidc = casProperties.getAuthn().getOidc();
        val discoveryConfig = oidc.getDiscovery();

        val discovery = new OidcServerDiscoverySettings(issuerService.determineIssuer(Optional.empty()));

        discovery.setClaimsSupported(new LinkedHashSet<>(discoveryConfig.getClaims()));
        discovery.setScopesSupported(new LinkedHashSet<>(discoveryConfig.getScopes()));
        discovery.setResponseTypesSupported(new LinkedHashSet<>(discoveryConfig.getResponseTypesSupported()));
        discovery.setResponseModesSupported(new LinkedHashSet<>(discoveryConfig.getResponseModesSupported()));
        discovery.setSubjectTypesSupported(new LinkedHashSet<>(discoveryConfig.getSubjectTypes()));
        discovery.setClaimTypesSupported(new LinkedHashSet<>(discoveryConfig.getClaimTypesSupported()));
        discovery.setIntrospectionSupportedAuthenticationMethods(
            new LinkedHashSet<>(discoveryConfig.getIntrospectionSupportedAuthenticationMethods()));
        discovery.setGrantTypesSupported(new LinkedHashSet<>(discoveryConfig.getGrantTypesSupported()));
        discovery.setTokenEndpointAuthMethodsSupported(
            new LinkedHashSet<>(discoveryConfig.getTokenEndpointAuthMethodsSupported()));
        discovery.setClaimsParameterSupported(discoveryConfig.isClaimsParameterSupported());
        discovery.setPromptValuesSupported(new LinkedHashSet<>(discoveryConfig.getPromptValuesSupported()));

        discovery.setIdTokenSigningAlgValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getIdTokenSigningAlgValuesSupported()));
        discovery.setIdTokenEncryptionAlgValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getIdTokenEncryptionAlgValuesSupported()));
        discovery.setIdTokenEncryptionEncodingValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getIdTokenEncryptionEncodingValuesSupported()));

        discovery.setBackchannelLogoutSupported(oidc.getLogout().isBackchannelLogoutSupported());
        discovery.setFrontchannelLogoutSupported(oidc.getLogout().isFrontchannelLogoutSupported());

        discovery.setDPopSigningAlgValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getDpopSigningAlgValuesSupported()));

        discovery.setUserInfoSigningAlgValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getUserInfoSigningAlgValuesSupported()));
        discovery.setUserInfoEncryptionAlgValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getUserInfoEncryptionAlgValuesSupported()));
        discovery.setUserInfoEncryptionEncodingValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getUserInfoEncryptionEncodingValuesSupported()));
        discovery.setCodeChallengeMethodsSupported(
            new LinkedHashSet<>(discoveryConfig.getCodeChallengeMethodsSupported()));

        discovery.setRequirePushedAuthorizationRequests(discoveryConfig.isRequirePushedAuthorizationRequests());
        discovery.setRequestParameterSupported(discoveryConfig.isRequestParameterSupported());
        discovery.setRequestUriParameterSupported(discoveryConfig.isRequestUriParameterSupported());
        discovery.setRequestObjectSigningAlgValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getRequestObjectSigningAlgValuesSupported()));
        discovery.setRequestObjectEncryptionAlgValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getRequestObjectEncryptionAlgValuesSupported()));
        discovery.setRequestObjectEncryptionEncodingValuesSupported(
            new LinkedHashSet<>(discoveryConfig.getRequestObjectEncryptionEncodingValuesSupported()));
        discovery.setAuthorizationResponseIssuerParameterSupported(
            discoveryConfig.isAuthorizationResponseIssuerParameterSupported());
        discovery.setAcrValuesSupported(new LinkedHashSet<>(discoveryConfig.getAcrValuesSupported()));
        if (discoveryConfig.getAcrValuesSupported().isEmpty()) {
            val providers = MultifactorAuthenticationUtils.getAvailableMultifactorAuthenticationProviders(applicationContext)
                .values()
                .stream()
                .map(MultifactorAuthenticationProvider::getId)
                .collect(Collectors.toSet());
            discovery.setAcrValuesSupported(providers);
        }
        return discovery;
    }

    @Override
    public Class<?> getObjectType() {
        return OidcServerDiscoverySettings.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
