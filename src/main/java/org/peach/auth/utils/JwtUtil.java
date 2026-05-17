package org.peach.auth.utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.peach.auth.code.AuthServerBizCode;
import org.peach.auth.dto.TokenDTO;
import org.peach.common.mvc.exception.BizException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT 签发工具（HS256）：将任意 {@link Object} 序列化为 JSON 写入标准 {@code sub}，不附加业务 Claim，便于复用。
 */
public final class JwtUtil {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * 固定 HMAC 秘钥：单段 UUID（与网关验签须一致时再统一改为配置或 KMS）。
 *
 * @author leiyangjun
 */
	public static final String DEFAULT_HMAC_SECRET = "550e8400-e29b-41d4-a716-446655440000";

	private JwtUtil() {
	}

	/**
	 * 使用默认秘钥签发 access token；{@code subjectPayload} 将转为 JSON 字符串作为 JWT {@code sub}。
	 *
	 * @param subjectPayload   任意可序列化对象（Jackson）；不可为 null
	 * @param expiresInSeconds 过期秒数
 *
 * @author leiyangjun
 */
	public static TokenDTO signAccessToken(Object subjectPayload, long expiresInSeconds) {
		return signAccessToken(subjectPayload, expiresInSeconds, DEFAULT_HMAC_SECRET);
	}

	/**
	 * 指定秘钥与有效期（秒）签发 access token。
	 *
	 * @param subjectPayload   任意可序列化对象；不可为 null
	 * @param expiresInSeconds 过期秒数
	 * @param secret           HMAC 原始字符串，UTF-8 字节长度须 ≥ 32
 *
 * @author leiyangjun
 */
	public static TokenDTO signAccessToken(Object subjectPayload, long expiresInSeconds, String secret) {
		Objects.requireNonNull(subjectPayload, "subjectPayload");
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			throw BizException.error(AuthServerBizCode.JWT_HMAC_SECRET_TOO_SHORT);
		}
		String subjectJson;
		try {
			subjectJson = OBJECT_MAPPER.writeValueAsString(subjectPayload);
		}
		catch (JsonProcessingException e) {
			throw BizException.error(AuthServerBizCode.JWT_SUBJECT_SERIALIZE_FAILED);
		}
		SecretKey key = Keys.hmacShaKeyFor(keyBytes);
		String tokenId = UUID.randomUUID().toString();
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plusSeconds(expiresInSeconds);

		String accessToken = Jwts.builder()
			.id(tokenId)
			.issuedAt(Date.from(issuedAt))
			.expiration(Date.from(expiresAt))
			.subject(subjectJson)
			.signWith(key)
			.compact();

		TokenDTO dto = new TokenDTO();
		dto.setTokenId(tokenId);
		dto.setTokenType("Bearer");
		dto.setAccessToken(accessToken);
		dto.setExpiresIn(expiresInSeconds);
		return dto;
	}
}
