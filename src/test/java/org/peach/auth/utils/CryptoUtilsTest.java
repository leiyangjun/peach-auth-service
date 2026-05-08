package org.peach.auth.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * BCrypt / RSA 工具类单测。
 */
class CryptoUtilsTest {

	@Test
	void bcryptEncodeAndMatches() {
		String hash = BCryptUtil.encode("secret123");
		assertTrue(BCryptUtil.matches("secret123", hash));
		assertFalse(BCryptUtil.matches("wrong", hash));
	}

	@Test
	void bcryptDifferentSalts() {
		String a = BCryptUtil.encode("same");
		String b = BCryptUtil.encode("same");
		assertNotEquals(a, b);
		assertTrue(BCryptUtil.matches("same", a));
		assertTrue(BCryptUtil.matches("same", b));
	}

	@Test
	void rsaClasspathKeyEncryptDecryptRoundTrip() {
		String plain = "hello-口令";
		String cipher = RSAUtil.encrypt(plain);
		assertEquals(plain, RSAUtil.decrypt(cipher));
	}

	@Test
	void rsaPublicKeyDtoFilled() {
		var dto = RSAUtil.getPublicKey();
		assertEquals("RSA", dto.getAlgorithm());
		assertEquals("RSA_PKCS1_V1_5", dto.getPadding());
		assertTrue(dto.getKeyBits() >= 2048);
		assertTrue(dto.getPublicKeyPem().contains("BEGIN PUBLIC KEY"));
	}
}
