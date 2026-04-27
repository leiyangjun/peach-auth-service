package org.peach.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CustomAuthSecurityConfig {

	@Bean
	public SecurityFilterChain permitAllSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll());
		http.formLogin((form) -> form.disable());
		http.httpBasic((basic) -> basic.disable());
		http.csrf((csrf) -> csrf.disable());
		return http.build();
	}
}
