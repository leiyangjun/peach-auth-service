package org.peach.auth.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@EnableConfigurationProperties(PeachAuthProperties.class)
public class PeachAuthConfiguration {

	@Bean
	public SecretKey peachJwtSecretKey(PeachAuthProperties properties) {
		byte[] bytes = properties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalArgumentException("peach.auth.jwt-secret 至少需要 32 字节");
		}
		return new SecretKeySpec(bytes, "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder(SecretKey peachJwtSecretKey) {
		return NimbusJwtEncoder.withSecretKey(peachJwtSecretKey).build();
	}

	@Bean
	public JwtDecoder jwtDecoder(SecretKey peachJwtSecretKey) {
		return NimbusJwtDecoder.withSecretKey(peachJwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
	}
}
