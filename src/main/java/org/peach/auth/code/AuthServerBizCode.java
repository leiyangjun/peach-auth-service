package org.peach.auth.code;

import org.peach.common.mvc.result.code.ApiResultCustomCode;

/**
 * 认证服务业务错误码与对外文案（提示段末两位 &gt; 20）。
 * <p>
 * 抛 {@link org.peach.common.mvc.exception.BizException} 时优先使用本枚举，避免散落硬编码字符串。
 * </p>
 */
public enum AuthServerBizCode implements ApiResultCustomCode {

	// ---------- 登录 / 参数（HTTP 400 场景）----------

	/** 4022：滑块校验失败或已过期 */
	SLIDER_VERIFY_FAILED(4022, "滑块校验失败或已过期"),

	/** 4023：无效用户或用户不存在 */
	UN_VALID_USER(4023, "无效用户或用户不存在"),

	/** 4024：用户名或密码错误 */
	LOGIN_BAD_CREDENTIALS(4024, "用户名或密码错误"),

	/** 4025：明文不能为空 */
	PLAIN_TEXT_REQUIRED(4025, "明文不能为空"),

	/** 4026：RSA 加密失败 */
	RSA_ENCRYPT_FAILED(4026, "RSA 加密失败"),

	/** 4027：密文不能为空 */
	RSA_CIPHER_REQUIRED(4027, "密文不能为空"),

	/** 4028：密文 Base64 无效 */
	RSA_CIPHER_BASE64_INVALID(4028, "密文 Base64 无效"),

	/** 4029：口令密文解密失败 */
	RSA_DECRYPT_FAILED(4029, "口令密文解密失败"),

	// ---------- 服务端配置 / 内部（HTTP 500 场景）----------

	/** 5021：JWT HMAC 秘钥长度不满足安全要求 */
	JWT_HMAC_SECRET_TOO_SHORT(5021, "JWT 秘钥 UTF-8 长度须至少 32 字节"),

	/** 5022：登录 RSA 私钥资源缺失、PEM 解析或密钥构造失败 */
	LOGIN_RSA_PRIVATE_KEY_INIT_FAILED(5022, "登录 RSA 私钥加载或解析失败");

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
