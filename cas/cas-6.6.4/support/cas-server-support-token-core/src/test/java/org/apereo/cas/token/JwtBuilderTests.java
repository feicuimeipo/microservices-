package org.apereo.cas.token;

import org.apereo.cas.mock.MockTicketGrantingTicket;

import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link JwtBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
@Tag("Tickets")
public class JwtBuilderTests {

    @Nested
    @SuppressWarnings("ClassCanBeStatic")
    public class DefaultTests extends BaseJwtTokenTicketBuilderTests {
        @Test
        public void verifyZonedDateTimeWorks() {
            val tgt = new MockTicketGrantingTicket("casuser");
            val jwt = tokenTicketBuilder.build(tgt, Map.of("date-time", List.of(ZonedDateTime.now(Clock.systemUTC()))));
            assertNotNull(jwt);
        }
    }

    @Nested
    @SuppressWarnings("ClassCanBeStatic")
    @TestPropertySource(properties = "cas.authn.token.crypto.enabled=false")
    public class DefaultCipherDisabled extends BaseJwtTokenTicketBuilderTests {
        @Test
        public void verifyUnknownJwt() {
            val jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIm9yZy5hcGVyZW8uY2FzLnNlcnZpY2VzLlJlZ2"
                      + "lzdGVyZWRTZXJ2aWNlIjoiMjEzMzI0ODYyMSIsImtpZCI6IjEyMzQ1Njc4OTAifQ"
                      + ".eyJzdWIiOiJjYXN1c2VyIiwic2NvcGUiOiJvcGVuaWQiLCJpc3MiOiJodHRwczpcL1wvY2FzLmV4Y"
                      + "W1wbGUub3JnIiwicmVzcG9uc2VfdHlwZSI6ImNvZGUiL"
                      + "CJyZWRpcmVjdF91cmkiOiJodHRwczpcL1wvYXBlcmVvLmdpdGh1Yi5pbyIsImlhdCI6MTY0NjczMTgxOS"
                      + "wianRpIjoiZWVkY2Q5Y2ItNDA1MS00ODAyLWFmYWUtYmFkMzU1NDNiYjU"
                      + "3IiwiY2xpZW50X2lkIjoiY2xpZW50In0"
                      + ".16XuMcIc68QSLeEfOdP6_hegZac-YI46tVfbeEhu6_fiPH5LxB4OOefTNuf0ST18scya18L3Da"
                      + "QLFQhdQkTneKa9dJt4fHl8POQ-IjpagaVWwFMGWM9VyVo_"
                      + "wd0rHd-1pg-OtnvH8PqSZuVoLm--eS0x7vQOX5IKedTXhACIQRZCq3Rxs9s9q1Rhjxv6hvkgWgrG42i5D6IE"
                      + "Uxs1y-a9HLySm2_pxvg_7PiaNIps85Le9mWSrOf_F761q1pKHIR5INDoItMAHWKgnDLjQg8R1WPCyeq7XMa"
                      + "cKeXDS4dYk0IeJPK1teyKWJrsdRBdzgnLVyM6MaFszHWOLv_U9Uy22g";

            assertThrows(IllegalArgumentException.class, () -> tokenTicketJwtBuilder.unpack(Optional.empty(), jwt));
        }
    }
}
