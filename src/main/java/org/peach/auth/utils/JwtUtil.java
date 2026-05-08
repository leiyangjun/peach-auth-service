package org.peach.auth.utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.peach.auth.code.AuthServerBizCode;
import org.peach.auth.dto.TokenDTO;
import org.peach.auth.entity.User;
import org.peach.common.mvc.exception.BizException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT 签发工具（HS256）。默认秘钥为固定标准 UUID 串（36 字节 UTF-8，满足 HMAC 长度 ≥32）；也可显式传入秘钥与有效期。
 */
public final class JwtUtil {

	private static final String CLAIM_TOKEN_USE = "token_use";

	/**
	 * 固定 HMAC 秘钥：单段 UUID（与网关验签须一致时再统一改为配置或 KMS）。
	 */
	public static final String DEFAULT_HMAC_SECRET = "550e8400-e29b-41d4-a716-446655440000";

	private JwtUtil() {
	}

	/**
	 * 使用默认秘钥签发 access token，并组装 {@link TokenDTO}（{@code tokenId} 与 JWT {@code jti} 一致）。
	 *
	 * @param user             登录用户（调用方保证非空且 {@code id} 有意义）
	 * @param expiresInSeconds 过期秒数（调用方保证合法，通常来自 Service 层 {@code @Value}）
	 */
	public static TokenDTO signAccessToken(User user, long expiresInSeconds) {
		return signAccessToken(user, expiresInSeconds, DEFAULT_HMAC_SECRET);
	}

	/**
	 * 指定秘钥与有效期（秒）签发 access token，并返回 {@link TokenDTO}。
	 *
	 * @param secret           HMAC 原始字符串，UTF-8 字节长度须 ≥ 32
	 * @param expiresInSeconds 过期秒数（调用方保证合法）
	 */
	public static TokenDTO signAccessToken(User user, long expiresInSeconds, String secret) {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			throw BizException.serverError(AuthServerBizCode.JWT_HMAC_SECRET_TOO_SHORT);
		}
		SecretKey key = Keys.hmacShaKeyFor(keyBytes);
		String tokenId = UUID.randomUUID().toString();
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plusSeconds(expiresInSeconds);

		var builder = Jwts.builder()
			.id(tokenId)
			.issuedAt(Date.from(issuedAt))
			.expiration(Date.from(expiresAt))
			.subject(String.valueOf(user.getId()))
			.claim("preferred_username", user.getUsername())
			.claim(CLAIM_TOKEN_USE, "access");
		if (user.getSubjectType() != null) {
			// builder.claim(CurrentLoginUserUtil.CLAIM_SUBJECT_TYPE, user.getSubjectType());
		}
		String accessToken = builder.signWith(key).compact();

		TokenDTO dto = new TokenDTO();
		dto.setTokenId(tokenId);
		dto.setTokenType("Bearer");
		dto.setAccessToken(accessToken);
		dto.setExpiresIn(expiresInSeconds);
		return dto;
	}
}
