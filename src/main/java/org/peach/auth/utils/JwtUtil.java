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
import org.peach.common.mvc.vo.LoginUserVO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT 签发与校验（HS256）：{@code sub} 为 LoginUserVO JSON；通过 {@link JwtTokenType#CLAIM_NAME} 区分 access / refresh。
 *
 * @author leiyangjun
 */
public final class JwtUtil {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * 固定 HMAC 秘钥：单段 UUID（与网关验签须一致时再统一改为配置或 KMS）。
	 */
	public static final String DEFAULT_HMAC_SECRET = "550e8400-e29b-41d4-a716-446655440000";

	private JwtUtil() {
	}

	/**
	 * 签发 access + refresh 双令牌（rotation：refresh 接口每次返回新 refresh）。
	 */
	public static TokenDTO signTokenPair(Object subjectPayload, long accessExpiresInSeconds, long refreshExpiresInSeconds) {
		return signTokenPair(subjectPayload, accessExpiresInSeconds, refreshExpiresInSeconds, DEFAULT_HMAC_SECRET);
	}

	public static TokenDTO signTokenPair(Object subjectPayload, long accessExpiresInSeconds, long refreshExpiresInSeconds,
			String secret) {
		TokenDTO access = signTypedToken(subjectPayload, accessExpiresInSeconds, JwtTokenType.ACCESS, secret);
		TokenDTO refresh = signTypedToken(subjectPayload, refreshExpiresInSeconds, JwtTokenType.REFRESH, secret);
		TokenDTO pair = new TokenDTO();
		pair.setTokenId(access.getTokenId());
		pair.setTokenType(access.getTokenType());
		pair.setAccessToken(access.getAccessToken());
		pair.setExpiresIn(access.getExpiresIn());
		pair.setRefreshToken(refresh.getAccessToken());
		pair.setRefreshExpiresIn(refresh.getExpiresIn());
		return pair;
	}

	/**
	 * 使用默认秘钥签发 access token；{@code subjectPayload} 将转为 JSON 字符串作为 JWT {@code sub}。
	 */
	public static TokenDTO signAccessToken(Object subjectPayload, long expiresInSeconds) {
		return signAccessToken(subjectPayload, expiresInSeconds, DEFAULT_HMAC_SECRET);
	}

	public static TokenDTO signAccessToken(Object subjectPayload, long expiresInSeconds, String secret) {
		return signTypedToken(subjectPayload, expiresInSeconds, JwtTokenType.ACCESS, secret);
	}

	private static TokenDTO signTypedToken(Object subjectPayload, long expiresInSeconds, String tokenType, String secret) {
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

		String compact = Jwts.builder()
			.id(tokenId)
			.issuedAt(Date.from(issuedAt))
			.expiration(Date.from(expiresAt))
			.subject(subjectJson)
			.claim(JwtTokenType.CLAIM_NAME, tokenType)
			.signWith(key)
			.compact();

		TokenDTO dto = new TokenDTO();
		dto.setTokenId(tokenId);
		dto.setTokenType("Bearer");
		dto.setAccessToken(compact);
		dto.setExpiresIn(expiresInSeconds);
		return dto;
	}

	/**
	 * 校验 refresh 令牌并解析为 {@link LoginUserVO}。
	 */
	public static LoginUserVO parseRefreshToken(String refreshToken) {
		return parseRefreshToken(refreshToken, DEFAULT_HMAC_SECRET);
	}

	public static LoginUserVO parseRefreshToken(String refreshToken, String secret) {
		if (refreshToken == null || refreshToken.isBlank()) {
			throw BizException.validWarn(AuthServerBizCode.REFRESH_TOKEN_INVALID);
		}
		try {
			Claims claims = parseClaims(refreshToken, secret);
			String typ = claims.get(JwtTokenType.CLAIM_NAME, String.class);
			if (!JwtTokenType.REFRESH.equals(typ)) {
				throw BizException.validWarn(AuthServerBizCode.REFRESH_TOKEN_INVALID);
			}
			return readSubject(claims.getSubject());
		}
		catch (ExpiredJwtException ex) {
			throw BizException.validWarn(AuthServerBizCode.REFRESH_TOKEN_EXPIRED);
		}
		catch (JwtException ex) {
			throw BizException.validWarn(AuthServerBizCode.REFRESH_TOKEN_INVALID);
		}
	}

	private static Claims parseClaims(String compactJwt, String secret) {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			throw BizException.error(AuthServerBizCode.JWT_HMAC_SECRET_TOO_SHORT);
		}
		SecretKey key = Keys.hmacShaKeyFor(keyBytes);
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(compactJwt).getPayload();
	}

	private static LoginUserVO readSubject(String subjectJson) {
		if (subjectJson == null || subjectJson.isBlank()) {
			throw BizException.validWarn(AuthServerBizCode.REFRESH_TOKEN_INVALID);
		}
		try {
			return OBJECT_MAPPER.readValue(subjectJson.trim(), LoginUserVO.class);
		}
		catch (JsonProcessingException e) {
			throw BizException.validWarn(AuthServerBizCode.REFRESH_TOKEN_INVALID);
		}
	}

}
