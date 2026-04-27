package org.peach.auth.service;

import java.time.Instant;
import java.util.UUID;

import org.peach.auth.config.PeachAuthProperties;
import org.peach.auth.dto.LoginTokenResponse;
import org.peach.auth.security.AccountDetails;
import org.peach.common.utils.CurrentLoginUserUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class PeachJwtTokenService {

	private final JwtEncoder jwtEncoder;
	private final PeachAuthProperties authProperties;

	public PeachJwtTokenService(JwtEncoder jwtEncoder, PeachAuthProperties authProperties) {
		this.jwtEncoder = jwtEncoder;
		this.authProperties = authProperties;
	}

	public LoginTokenResponse issue(Authentication authentication) {
		Instant issuedAt = Instant.now();
		Instant accessExpiresAt = issuedAt.plusSeconds(this.authProperties.getAccessTokenTtlSeconds());
		Instant refreshExpiresAt = issuedAt.plusSeconds(this.authProperties.getRefreshTokenTtlSeconds());

		String subject = authentication.getName();
		String preferredUsername = authentication.getName();
		String subjectType = null;

		Object principal = authentication.getPrincipal();
		if (principal instanceof AccountDetails accountDetails) {
			subject = String.valueOf(accountDetails.getUserId());
			preferredUsername = accountDetails.getUsername();
			subjectType = accountDetails.getUser().getSubjectType();
		}

		JwtClaimsSet.Builder accessClaims = JwtClaimsSet.builder()
				.id(UUID.randomUUID().toString())
				.issuer(this.authProperties.getIssuer())
				.issuedAt(issuedAt)
				.expiresAt(accessExpiresAt)
				.subject(subject)
				.claim("preferred_username", preferredUsername)
				.claim("token_use", "access");
		if (subjectType != null) {
			accessClaims.claim(CurrentLoginUserUtil.CLAIM_SUBJECT_TYPE, subjectType);
		}

		String accessToken = this.jwtEncoder.encode(
				JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), accessClaims.build())).getTokenValue();
		JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
				.id(UUID.randomUUID().toString())
				.issuer(this.authProperties.getIssuer())
				.issuedAt(issuedAt)
				.expiresAt(refreshExpiresAt)
				.subject(subject)
				.claim("token_use", "refresh")
				.build();
		String refreshToken = this.jwtEncoder.encode(
				JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), refreshClaims)).getTokenValue();

		LoginTokenResponse response = new LoginTokenResponse();
		response.setTokenType("Bearer");
		response.setAccessToken(accessToken);
		response.setAccessTokenExpiresIn(this.authProperties.getAccessTokenTtlSeconds());
		response.setRefreshToken(refreshToken);
		response.setRefreshTokenExpiresIn(this.authProperties.getRefreshTokenTtlSeconds());
		return response;
	}
}
