package org.peach.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "peach.auth")
public class PeachAuthProperties {

	private String issuer = "http://127.0.0.1:8084";
	private long accessTokenTtlSeconds = 7200;
	private long refreshTokenTtlSeconds = 604800;
	private String jwtSecret = "please-change-this-to-32-plus-bytes-secret";

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public long getAccessTokenTtlSeconds() {
		return accessTokenTtlSeconds;
	}

	public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
		this.accessTokenTtlSeconds = accessTokenTtlSeconds;
	}

	public long getRefreshTokenTtlSeconds() {
		return refreshTokenTtlSeconds;
	}

	public void setRefreshTokenTtlSeconds(long refreshTokenTtlSeconds) {
		this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
	}

	public String getJwtSecret() {
		return jwtSecret;
	}

	public void setJwtSecret(String jwtSecret) {
		this.jwtSecret = jwtSecret;
	}
}
