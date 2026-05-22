package org.peach.auth.code;

import org.peach.common.mvc.result.code.MessageCode;

/**
 * 认证服务业务错误码（HTTP 400 消息码段须在 4100–4999，HTTP 500 须在 5100–5999）。
 * <p>
 * 抛 {@link org.peach.common.mvc.exception.BizException} 时使用 {@link org.peach.common.mvc.exception.BizException#validWarn(MessageCode)}
 * / {@link org.peach.common.mvc.exception.BizException#error(MessageCode)}，勿引用启动器内框架内置枚举作为本服务业务码。
 * </p>
 *
 * @author leiyangjun
 */
public enum AuthServerBizCode implements MessageCode {

	// ---------- 登录 / 参数（HTTP 400）----------

	/** 滑块校验失败或已过期 */
	SLIDER_VERIFY_FAILED(4100, "滑块校验失败或已过期"),

	/** 无效用户或用户不存在 */
	UN_VALID_USER(4101, "无效用户或用户不存在"),

	/** 用户名或密码错误 */
	LOGIN_BAD_CREDENTIALS(4102, "用户名或密码错误"),

	/** 明文不能为空 */
	PLAIN_TEXT_REQUIRED(4103, "明文不能为空"),

	/** RSA 加密失败 */
	RSA_ENCRYPT_FAILED(4104, "RSA 加密失败"),

	/** 密文不能为空 */
	RSA_CIPHER_REQUIRED(4105, "密文不能为空"),

	/** 密文 Base64 无效 */
	RSA_CIPHER_BASE64_INVALID(4106, "密文 Base64 无效"),

	/** 口令密文解密失败 */
	RSA_DECRYPT_FAILED(4107, "口令密文解密失败"),

	/** 刷新令牌无效或签名错误 */
	REFRESH_TOKEN_INVALID(4108, "刷新令牌无效或已失效"),

	/** 刷新令牌已过期 */
	REFRESH_TOKEN_EXPIRED(4109, "刷新令牌已过期"),

	// ---------- 服务端配置 / 内部（HTTP 500）----------

	/** JWT HMAC 秘钥长度不满足安全要求 */
	JWT_HMAC_SECRET_TOO_SHORT(5100, "JWT 秘钥 UTF-8 长度须至少 32 字节"),

	/** 登录 RSA 私钥资源缺失、PEM 解析或密钥构造失败 */
	LOGIN_RSA_PRIVATE_KEY_INIT_FAILED(5101, "登录 RSA 私钥加载或解析失败"),

	/** JWT subject 载荷序列化为 JSON 失败 */
	JWT_SUBJECT_SERIALIZE_FAILED(5102, "JWT subject 序列化失败");

	private final int code;
	private final String msg;

	AuthServerBizCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public int code() {
		return code;
	}

	@Override
	public String msg() {
		return msg;
	}
}
