package org.peach.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务启动入口。
 * <p>
 * 创作日期：2026-04-29，作者：leiyangjun — 启用注册发现客户端，将实例注册到 Nacos，
 * 供网关与其他服务按服务名发现调用。
 * </p>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthApp {

	/**
	 * 应用主入口。
	 *
	 * @param args 启动参数
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthApp.class, args);
	}
}
