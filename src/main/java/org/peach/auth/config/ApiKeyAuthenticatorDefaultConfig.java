package org.peach.auth.config;

import org.peach.auth.security.PeachApiKeyAuthenticator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyAuthenticatorDefaultConfig {

	@Bean
	@ConditionalOnMissingBean(PeachApiKeyAuthenticator.class)
	public PeachApiKeyAuthenticator rejectingPeachApiKeyAuthenticator() {
		return apiKey -> null;
	}
}
