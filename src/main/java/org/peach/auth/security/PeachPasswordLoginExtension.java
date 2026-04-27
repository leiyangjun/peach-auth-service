package org.peach.auth.security;

import java.util.Map;

/**
 * 密码登录校验扩展点：可接验证码、风控、多因子。
 */
public interface PeachPasswordLoginExtension {

	void beforePasswordAuthentication(String username, Map<String, Object> additionalParameters);
}
