package org.peach.auth.utils;

/**
 * JWT 令牌类型 Claim 取值（与网关验签 access 无关，仅 auth-service 签发/校验 refresh 时使用）。
 *
 * @author leiyangjun
 */
public final class JwtTokenType {

	/** Claim 名：{@value} */
	public static final String CLAIM_NAME = "peach_token_type";

	public static final String ACCESS = "access";

	public static final String REFRESH = "refresh";

	private JwtTokenType() {
	}

}
