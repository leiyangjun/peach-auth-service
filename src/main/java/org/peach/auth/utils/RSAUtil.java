package org.peach.auth.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.peach.auth.code.AuthServerBizCode;
import org.peach.auth.dto.RsaPublicKeyDTO;
import org.peach.common.mvc.exception.BizException;

/**
 * 登录口令 RSA（PKCS#1 v1.5，与常见前端 JSEncrypt 一致）。私钥来自类路径固定 {@code rsa_private.pem}。
 */
public final class RSAUtil {

	private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

	private static final String PEM_RESOURCE = "rsa_private.pem";

	private static final PrivateKey PRIVATE_KEY;
	private static final PublicKey PUBLIC_KEY;
	private static final String PUBLIC_KEY_PEM;
	private static final int KEY_BITS;

	static {
		try (InputStream in = RSAUtil.class.getClassLoader().getResourceAsStream(PEM_RESOURCE)) {
			if (in == null) {
				throw BizException.serverError(AuthServerBizCode.LOGIN_RSA_PRIVATE_KEY_INIT_FAILED);
			}
			String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			PRIVATE_KEY = privateKeyFromPem(pem);
			PUBLIC_KEY = publicKeyFromPrivate(PRIVATE_KEY);
			KEY_BITS = ((RSAPrivateKey) PRIVATE_KEY).getModulus().bitLength();
			PUBLIC_KEY_PEM = toPublicKeyPem(PUBLIC_KEY);
		}
		catch (IOException ex) {
			throw BizException.serverError(AuthServerBizCode.LOGIN_RSA_PRIVATE_KEY_INIT_FAILED, ex);
		}
	}

	private RSAUtil() {
	}

	/**
	 * 使用内置公钥加密 UTF-8 明文，返回 Base64 密文。
	 */
	public static String encrypt(String plainText) {
		if (plainText == null) {
			throw BizException.badRequest(AuthServerBizCode.PLAIN_TEXT_REQUIRED);
		}
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, PUBLIC_KEY);
			byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(cipherBytes);
		}
		catch (Exception ex) {
			throw BizException.badRequest(AuthServerBizCode.RSA_ENCRYPT_FAILED);
		}
	}

	/**
	 * 使用内置私钥将 Base64 密文解密为 UTF-8 明文。
	 */
	public static String decrypt(String base64CipherText) {
		if (base64CipherText == null || base64CipherText.isBlank()) {
			throw BizException.badRequest(AuthServerBizCode.RSA_CIPHER_REQUIRED);
		}
		try {
			byte[] cipherBytes = Base64.getDecoder().decode(base64CipherText.trim());
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, PRIVATE_KEY);
			byte[] plain = cipher.doFinal(cipherBytes);
			return new String(plain, StandardCharsets.UTF_8);
		}
		catch (IllegalArgumentException ex) {
			throw BizException.badRequest(AuthServerBizCode.RSA_CIPHER_BASE64_INVALID);
		}
		catch (Exception ex) {
			throw BizException.badRequest(AuthServerBizCode.RSA_DECRYPT_FAILED);
		}
	}

	/**
	 * 返回供前端加密口令使用的公钥信息。
	 */
	public static RsaPublicKeyDTO getPublicKey() {
		RsaPublicKeyDTO dto = new RsaPublicKeyDTO();
		dto.setAlgorithm("RSA");
		dto.setPadding("RSA_PKCS1_V1_5");
		dto.setKeyBits(KEY_BITS);
		dto.setPublicKeyPem(PUBLIC_KEY_PEM);
		return dto;
	}

	private static PublicKey publicKeyFromPrivate(PrivateKey privateKey) {
		if (!(privateKey instanceof RSAPrivateCrtKey crt)) {
			throw BizException.serverError(AuthServerBizCode.LOGIN_RSA_PRIVATE_KEY_INIT_FAILED);
		}
		try {
			var spec = new RSAPublicKeySpec(crt.getModulus(), crt.getPublicExponent());
			return KeyFactory.getInstance("RSA").generatePublic(spec);
		}
		catch (Exception ex) {
			throw BizException.serverError(AuthServerBizCode.LOGIN_RSA_PRIVATE_KEY_INIT_FAILED, ex);
		}
	}

	private static PrivateKey privateKeyFromPem(String pem) {
		byte[] der = parsePemDer(pem, "PRIVATE KEY");
		try {
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
			return KeyFactory.getInstance("RSA").generatePrivate(spec);
		}
		catch (Exception ex) {
			throw BizException.serverError(AuthServerBizCode.LOGIN_RSA_PRIVATE_KEY_INIT_FAILED, ex);
		}
	}

	private static String toPublicKeyPem(PublicKey publicKey) {
		String b64 = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(publicKey.getEncoded());
		return "-----BEGIN PUBLIC KEY-----\n" + b64 + "\n-----END PUBLIC KEY-----\n";
	}

	private static byte[] parsePemDer(String pem, String type) {
		String stripped = pem.replace("\r\n", "\n").trim();
		String begin = "-----BEGIN " + type + "-----";
		String end = "-----END " + type + "-----";
		if (!stripped.contains(begin)) {
			throw BizException.serverError(AuthServerBizCode.LOGIN_RSA_PRIVATE_KEY_INIT_FAILED);
		}
		stripped = extractBetween(stripped, begin, end);
		stripped = stripped.replaceAll("\\s+", "");
		try {
			return Base64.getDecoder().decode(stripped);
		}
		catch (IllegalArgumentException ex) {
			throw BizException.serverError(AuthServerBizCode.LOGIN_RSA_PRIVATE_KEY_INIT_FAILED, ex);
		}
	}

	private static String extractBetween(String text, String start, String end) {
		int i = text.indexOf(start);
		int j = text.indexOf(end);
		if (i < 0 || j < 0 || j <= i) {
			throw BizException.serverError(AuthServerBizCode.LOGIN_RSA_PRIVATE_KEY_INIT_FAILED);
		}
		return text.substring(i + start.length(), j);
	}
}
