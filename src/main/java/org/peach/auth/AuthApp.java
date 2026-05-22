package org.peach.auth;

import org.peach.common.mvc.bootstrap.annotation.PeachCloud;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 认证服务启动类。
 *
 * @author leiyangjun
 */
@PeachCloud
@EnableScheduling
public class AuthApp {

	public static void main(String[] args) {
		SpringApplication.run(AuthApp.class, args);
	}
}
