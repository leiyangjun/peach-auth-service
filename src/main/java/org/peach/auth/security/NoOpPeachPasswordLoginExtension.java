package org.peach.auth.security;

import java.util.Map;

import org.peach.auth.service.SliderCaptchaService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class NoOpPeachPasswordLoginExtension implements PeachPasswordLoginExtension {

	private final SliderCaptchaService sliderCaptchaService;

	public NoOpPeachPasswordLoginExtension(SliderCaptchaService sliderCaptchaService) {
		this.sliderCaptchaService = sliderCaptchaService;
	}

	@Override
	public void beforePasswordAuthentication(String username, Map<String, Object> additionalParameters) {
		Object captchaIdObj = additionalParameters.get("captchaId");
		Object sliderOffsetObj = additionalParameters.get("sliderOffset");
		if (captchaIdObj == null || sliderOffsetObj == null) {
			throw new BadCredentialsException("滑块校验缺失");
		}
		String captchaId = String.valueOf(captchaIdObj);
		int sliderOffset;
		try {
			sliderOffset = Integer.parseInt(String.valueOf(sliderOffsetObj));
		}
		catch (NumberFormatException ex) {
			throw new BadCredentialsException("滑块参数非法");
		}
		if (!this.sliderCaptchaService.verifyAndConsume(captchaId, sliderOffset)) {
			throw new BadCredentialsException("滑块校验失败");
		}
	}
}
