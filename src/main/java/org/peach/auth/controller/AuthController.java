package org.peach.auth.controller;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.peach.auth.dto.ApiKeyLoginRequest;
import org.peach.auth.dto.LoginTokenResponse;
import org.peach.auth.dto.PasswordLoginRequest;
import org.peach.auth.dto.SliderCaptchaChallengeResponse;
import org.peach.auth.security.PeachApiKeyAuthenticator;
import org.peach.auth.security.PeachPasswordLoginExtension;
import org.peach.auth.service.PeachJwtTokenService;
import org.peach.auth.service.SliderCaptchaService;
import org.peach.common.mvc.result.ApiResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/login")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final PeachJwtTokenService peachJwtTokenService;
	private final PeachPasswordLoginExtension peachPasswordLoginExtension;
	private final PeachApiKeyAuthenticator peachApiKeyAuthenticator;
	private final SliderCaptchaService sliderCaptchaService;

	public AuthController(AuthenticationManager authenticationManager, PeachJwtTokenService peachJwtTokenService,
			PeachPasswordLoginExtension peachPasswordLoginExtension, PeachApiKeyAuthenticator peachApiKeyAuthenticator,
			SliderCaptchaService sliderCaptchaService) {
		this.authenticationManager = authenticationManager;
		this.peachJwtTokenService = peachJwtTokenService;
		this.peachPasswordLoginExtension = peachPasswordLoginExtension;
		this.peachApiKeyAuthenticator = peachApiKeyAuthenticator;
		this.sliderCaptchaService = sliderCaptchaService;
	}

	@PostMapping("/slider/challenge")
	public ApiResult<SliderCaptchaChallengeResponse> sliderChallenge() {
		return ApiResult.ok(this.sliderCaptchaService.createChallenge());
	}

	@PostMapping("/password")
	public ApiResult<LoginTokenResponse> password(@RequestBody PasswordLoginRequest request) {
		if (request == null || StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
			return ApiResult.fail400("username/password 不能为空");
		}
		Map<String, Object> additional = request.getAdditionalParameters() == null ? Collections.emptyMap()
				: request.getAdditionalParameters();
		this.peachPasswordLoginExtension.beforePasswordAuthentication(request.getUsername(), additional);
		try {
			Authentication authenticated = authenticatePassword(request.getUsername(), request.getPassword());
			return ApiResult.ok(this.peachJwtTokenService.issue(authenticated));
		}
		catch (AuthenticationException ex) {
			return ApiResult.fail401();
		}
	}

	@PostMapping("/api-key")
	public ApiResult<LoginTokenResponse> apiKey(@RequestBody ApiKeyLoginRequest request) {
		if (request == null || StringUtils.isBlank(request.getApiKey())) {
			return ApiResult.fail400("apiKey 不能为空");
		}
		Authentication authenticated = this.peachApiKeyAuthenticator.authenticate(request.getApiKey());
		if (authenticated == null || !authenticated.isAuthenticated()) {
			return ApiResult.fail401();
		}
		return ApiResult.ok(this.peachJwtTokenService.issue(authenticated));
	}

	private Authentication authenticatePassword(String username, String password) {
		UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
		return this.authenticationManager.authenticate(token);
	}
}
