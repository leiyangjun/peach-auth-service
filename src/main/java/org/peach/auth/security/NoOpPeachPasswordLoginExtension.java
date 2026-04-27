package org.peach.auth.security;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class NoOpPeachPasswordLoginExtension implements PeachPasswordLoginExtension {

	@Override
	public void beforePasswordAuthentication(String username, Map<String, Object> additionalParameters) {
		// 默认不校验
	}
}
