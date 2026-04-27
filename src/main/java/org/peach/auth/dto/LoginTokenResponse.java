package org.peach.auth.dto;

public class LoginTokenResponse {

	private String tokenType;
	private String accessToken;
	private long accessTokenExpiresIn;
	private String refreshToken;
	private long refreshTokenExpiresIn;

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getAccessTokenExpiresIn() {
		return accessTokenExpiresIn;
	}

	public void setAccessTokenExpiresIn(long accessTokenExpiresIn) {
		this.accessTokenExpiresIn = accessTokenExpiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public long getRefreshTokenExpiresIn() {
		return refreshTokenExpiresIn;
	}

	public void setRefreshTokenExpiresIn(long refreshTokenExpiresIn) {
		this.refreshTokenExpiresIn = refreshTokenExpiresIn;
	}
}
