package org.apereo.cas.uma.ticket.rpt;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.uma.web.controllers.BaseUmaEndpointControllerTests;
import org.apereo.cas.util.jwt.JsonWebTokenSigner;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link UmaRequestingPartyTokenSigningServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@Tag("UMA")
public class UmaRequestingPartyTokenSigningServiceTests extends BaseUmaEndpointControllerTests {
    @Test
    public void verifyUnknownJwks() {
        val props = new CasConfigurationProperties();
        props.getAuthn().getOauth().getUma().getCore().setIssuer("cas");
        val jwks = new ClassPathResource("nothing.jwks");
        props.getAuthn().getOauth().getUma().getRequestingPartyToken().getJwksFile().setLocation(jwks);
        val signingService = new UmaRequestingPartyTokenSigningService(props);
        assertNull(signingService.getJsonWebKeySigningKey());
        val service = getRegisteredService(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        assertEquals(JsonWebTokenSigner.ALGORITHM_ALL_EXCEPT_NONE, signingService.getAllowedSigningAlgorithms(service));
    }
}
