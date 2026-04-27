package org.peach.auth.config;

import org.peach.auth.security.AccountUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CustomAuthenticationManagerConfig {

	@Bean
	public AuthenticationManager authenticationManager(AccountUserDetailsService accountUserDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(accountUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider::authenticate;
	}
}
