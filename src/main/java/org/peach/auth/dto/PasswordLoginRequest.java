package org.peach.auth.dto;

import java.util.Map;

public class PasswordLoginRequest {

	private String username;
	private String password;
	private Map<String, Object> additionalParameters;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, Object> getAdditionalParameters() {
		return additionalParameters;
	}

	public void setAdditionalParameters(Map<String, Object> additionalParameters) {
		this.additionalParameters = additionalParameters;
	}
}
