package org.apereo.cas.oidc.token;

import org.apereo.cas.oidc.issuer.OidcIssuerService;
import org.apereo.cas.oidc.jwks.OidcJsonWebKeyCacheKey;
import org.apereo.cas.oidc.jwks.OidcJsonWebKeyUsage;
import org.apereo.cas.oidc.jwks.rotation.OidcJsonWebKeystoreRotationService;
import org.apereo.cas.services.OidcRegisteredService;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.ticket.BaseTokenSigningAndEncryptionService;
import org.apereo.cas.token.JwtBuilder;
import org.apereo.cas.util.EncodingUtils;
import org.apereo.cas.util.function.FunctionUtils;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTParser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.lambda.Unchecked;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwt.JwtClaims;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * This is {@link BaseOidcJsonWebKeyTokenSigningAndEncryptionService}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseOidcJsonWebKeyTokenSigningAndEncryptionService extends BaseTokenSigningAndEncryptionService {
    /**
     * The default keystore for OIDC tokens.
     */
    protected final LoadingCache<OidcJsonWebKeyCacheKey, Optional<JsonWebKeySet>> defaultJsonWebKeystoreCache;

    /**
     * The service keystore for OIDC tokens.
     */
    protected final LoadingCache<OidcJsonWebKeyCacheKey, Optional<JsonWebKeySet>> serviceJsonWebKeystoreCache;

    /**
     * Issuer service.
     */
    protected final OidcIssuerService issuerService;

    @Override
    public String encode(final OAuthRegisteredService service, final JwtClaims claims) {
        return FunctionUtils.doUnchecked(() -> {
            LOGGER.trace("Attempting to produce token generated for service [{}] with claims [{}]", service, claims.toJson());
            var innerJwt = signTokenIfNecessary(claims, service);
            if (shouldEncryptToken(service)) {
                innerJwt = encryptToken(service, innerJwt);
            }
            return innerJwt;
        });
    }

    @Override
    public JwtClaims decode(final String token, final Optional<OAuthRegisteredService> service) {
        return Unchecked.supplier(() -> {
            if (service.isPresent()) {
                val jwt = JWTParser.parse(token);
                if (jwt instanceof EncryptedJWT) {
                    val encryptionKey = getJsonWebKeyForEncryption(service.get());
                    val decoded = EncodingUtils.decryptJwtValue(encryptionKey.getPrivateKey(), token);
                    return super.decode(decoded, service);
                }
            }
            return super.decode(token, service);
        }, throwable -> {
            throw new IllegalArgumentException(throwable);
        }).get();
    }

    @Override
    public String resolveIssuer(final Optional<OAuthRegisteredService> service) {
        val filter = service
            .filter(svc -> svc instanceof OidcRegisteredService)
            .map(OidcRegisteredService.class::cast)
            .stream()
            .findFirst();
        return issuerService.determineIssuer(filter);
    }

    /**
     * Encrypt token.
     *
     * @param svc   the svc
     * @param token the inner jwt
     * @return the string
     */
    protected abstract String encryptToken(OAuthRegisteredService svc, String token);

    @Override
    protected PublicJsonWebKey getJsonWebKeySigningKey() {
        val iss = issuerService.determineIssuer(Optional.empty());
        LOGGER.trace("Using issuer [{}] to locate JWK signing key", iss);
        val jwks = defaultJsonWebKeystoreCache.get(new OidcJsonWebKeyCacheKey(iss, OidcJsonWebKeyUsage.SIGNING));
        if (Objects.requireNonNull(jwks).isEmpty()) {
            throw new IllegalArgumentException("No signing key could be found for issuer " + iss);
        }
        return (PublicJsonWebKey) jwks.get().getJsonWebKeys().get(0);
    }

    /**
     * Gets json web key for encryption.
     *
     * @param svc the svc
     * @return the json web key for encryption
     */
    protected PublicJsonWebKey getJsonWebKeyForEncryption(final OAuthRegisteredService svc) {
        LOGGER.debug("Service [{}] is set to encrypt tokens", svc);
        val jwks = serviceJsonWebKeystoreCache.get(new OidcJsonWebKeyCacheKey(svc, OidcJsonWebKeyUsage.ENCRYPTION));
        if (Objects.requireNonNull(jwks).isEmpty()) {
            throw new IllegalArgumentException(
                "Service " + svc.getServiceId()
                + " with client id " + svc.getClientId()
                + " is configured to encrypt tokens, yet no JSON web key is available to handle encryption");
        }
        val jsonWebKey = jwks.get()
            .getJsonWebKeys()
            .stream()
            .filter(key -> OidcJsonWebKeystoreRotationService.JsonWebKeyLifecycleStates.getJsonWebKeyState(key).isCurrent())
            .min(Comparator.comparing(JsonWebKey::getKeyId))
            .orElseThrow(() -> new IllegalArgumentException("Cannot locate current JSON web key for encryption"));
        LOGGER.debug("Found JSON web key to encrypt the token: [{}]", jsonWebKey);
        Objects.requireNonNull(jsonWebKey.getKey(), "JSON web key used to encrypt the token has no associated public key");
        return (PublicJsonWebKey) jsonWebKey;
    }

    private String signTokenIfNecessary(final JwtClaims claims, final OAuthRegisteredService svc) {
        if (shouldSignToken(svc)) {
            LOGGER.debug("Fetching JSON web key to sign the token for : [{}]", svc.getClientId());
            val jsonWebKey = getJsonWebKeySigningKey();
            LOGGER.debug("Found JSON web key to sign the token: [{}]", jsonWebKey);
            Objects.requireNonNull(jsonWebKey.getPrivateKey(), "JSON web key used to sign the token has no associated private key");
            return signToken(svc, claims, jsonWebKey);
        }
        val claimSet = JwtBuilder.parse(claims.toJson());
        return JwtBuilder.buildPlain(claimSet, Optional.of(svc));
    }
}
