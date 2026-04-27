package org.peach.auth.security;

import org.springframework.security.core.Authentication;

/**
 * API Key 认证扩展点：将 apiKey 转换为已认证身份。
 */
public interface PeachApiKeyAuthenticator {

	Authentication authenticate(String apiKey);
}
