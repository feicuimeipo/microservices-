package org.apereo.cas.util.jwt;

import org.apereo.cas.util.gen.DefaultRandomStringGenerator;

import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTParser;
import lombok.val;
import org.jose4j.keys.AesKey;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link JsonWebTokenEncryptorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.6.0
 */
@Tag("Utility")
public class JsonWebTokenEncryptorTests {
    @Test
    public void verifyEncryptionFails() {
        val secret = new DefaultRandomStringGenerator().getNewString(16);
        val key = new AesKey(secret.getBytes(StandardCharsets.UTF_8));
        val encryptor = JsonWebTokenEncryptor.builder()
            .key(key)
            .headers(Map.of("name", "value"))
            .algorithm("dir")
            .keyId(UUID.randomUUID().toString())
            .allowedContentEncryptionAlgorithms(Set.of("A256GCM"))
            .encryptionMethod("A128GCM")
            .build();
        assertThrows(IllegalArgumentException.class, () -> encryptor.encrypt("ThisIsATest"));
    }

    @Test
    public void verifyA128GCM() throws Exception {
        val secret = new DefaultRandomStringGenerator().getNewString(16);
        val key = new AesKey(secret.getBytes(StandardCharsets.UTF_8));
        val result = JsonWebTokenEncryptor.builder()
            .key(key)
            .headers(Map.of("name", "value"))
            .algorithm("dir")
            .encryptionMethod("A128GCM")
            .build()
            .encrypt("ThisIsATest");
        assertTrue(JWTParser.parse(result) instanceof EncryptedJWT);
    }

    @Test
    public void verifyA512GCM() throws Exception {
        val secret = new DefaultRandomStringGenerator().getNewString(32);
        val key = new AesKey(secret.getBytes(StandardCharsets.UTF_8));
        val result = JsonWebTokenEncryptor.builder()
            .key(key)
            .headers(Map.of("name", "value"))
            .algorithm("dir")
            .encryptionMethod("A256GCM")
            .build()
            .encrypt("ThisIsATest");
        assertTrue(JWTParser.parse(result) instanceof EncryptedJWT);
    }

    @Test
    public void verifyA256KW() throws Exception {
        val secret = new DefaultRandomStringGenerator().getNewString(32);
        val key = new AesKey(secret.getBytes(StandardCharsets.UTF_8));
        val result = JsonWebTokenEncryptor.builder()
            .key(key)
            .headers(Map.of("name", "value"))
            .algorithm("A256KW")
            .encryptionMethod("A256GCM")
            .build()
            .encrypt("ThisIsATest");
        assertTrue(JWTParser.parse(result) instanceof EncryptedJWT);
    }
}


