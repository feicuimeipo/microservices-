package org.apereo.cas.util.cipher;

import org.apereo.cas.util.RandomUtils;

import lombok.val;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link JasyptNumberCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.6.0
 */
@Tag("Cipher")
public class JasyptNumberCipherExecutorTests {

    @Test
    public void verifyLongOperation() {
        val cipher = new JasyptNumberCipherExecutor(UUID.randomUUID().toString(), "My Cipher");
        assertEquals("My Cipher", cipher.getName());
        val randomNumber = RandomUtils.nextLong(0, 999999999);
        val encoded = cipher.encode(BigInteger.valueOf(randomNumber));
        assertEquals(randomNumber, cipher.decode(encoded).intValue());
    }

    @Test
    public void verifyIntOperation() {
        val cipher = new JasyptNumberCipherExecutor(UUID.randomUUID().toString(), "My Cipher");
        val randomNumber = RandomUtils.nextInt(0, 9999999);
        val encoded = cipher.encode(randomNumber);
        assertEquals(randomNumber, cipher.decode(encoded).intValue());
    }

    @Test
    public void verifyDecodeTwice() {
        val cipher = new JasyptNumberCipherExecutor(UUID.randomUUID().toString(), "My Cipher");
        val randomNumber = RandomUtils.nextInt(0, 9999999);
        val encoded = cipher.encode(randomNumber);
        assertThrows(EncryptionOperationNotPossibleException.class, () -> cipher.decode(cipher.decode(encoded)));
    }
}
