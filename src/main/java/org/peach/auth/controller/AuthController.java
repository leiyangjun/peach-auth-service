package org.peach.auth.controller;

import org.peach.auth.dto.LoginPasswordDTO;
import org.peach.auth.dto.TokenDTO;
import org.peach.auth.dto.RsaPublicKeyDTO;
import org.peach.auth.utils.RSAUtil;
import org.peach.auth.service.SliderCaptchaService;
import org.peach.auth.service.UserService;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mvc.validation.AutoValidated;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证登录 API（密码须 RSA 密文）。
 */
@RestController
@RequestMapping("/auth/login")
@AutoValidated
@Tag(name = "认证登录", description = "滑块、RSA 公钥、密码登录")
public class AuthController {

	private final SliderCaptchaService sliderCaptchaService;
	private final UserService userService;

	public AuthController(SliderCaptchaService sliderCaptchaService, UserService userService) {
		this.sliderCaptchaService = sliderCaptchaService;
		this.userService = userService;
	}

	@Operation(summary = "获取滑块挑战")
	@PostMapping("/slider/challenge")
	public ApiResult<SliderCaptchaService.SliderCaptchaChallengeResponse> sliderChallenge() {
		return ApiResult.ok(this.sliderCaptchaService.createChallenge());
	}

	@Operation(summary = "获取登录口令 RSA 公钥", description = "前端加密口令后提交 Base64 密文，禁止传明文")
	@GetMapping("/password/public-key")
	public ApiResult<RsaPublicKeyDTO> loginPasswordPublicKey() {
		return ApiResult.ok(RSAUtil.getPublicKey());
	}

	@Operation(summary = "用户名密码登录", description = "passwordCipher 为 RSA 密文（Base64）；须先通过滑块")
	@PostMapping("/password")
	public ApiResult<TokenDTO> loginByPassword(@RequestBody LoginPasswordDTO body) {
		TokenDTO token = this.userService.loginByPassword(body);
		return ApiResult.ok(token);
	}
}
