package org.peach.auth.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.peach.auth.code.AuthServerBizCode;
import org.peach.common.core.exception.BizException;

/**
 * BCrypt 口令工具：生成摘要（入库）与验密（登录）。
 * <p>
 * BCrypt 为单向摘要而非可逆加密；与常见 Spring Security / jBCrypt 存库格式兼容。
 * </p>
 */
public final class BCryptUtil {

	/** 默认计算成本（轮数），一般 10～12 */
	public static final int DEFAULT_COST = 10;

	private BCryptUtil() {
	}

	/**
	 * 对明文口令生成 BCrypt 摘要，用于写入数据库等持久化场景。
	 *
	 * @param plainText 明文口令
	 * @return BCrypt 哈希字符串（含盐）
 *
 * @author leiyangjun
 */
	public static String encode(String plainText) {
		if (plainText == null) {
			throw BizException.validWarn(AuthServerBizCode.PLAIN_TEXT_REQUIRED);
		}
		return BCrypt.hashpw(plainText, BCrypt.gensalt(DEFAULT_COST));
	}

	/**
	 * 指定成本生成摘要（成本越高越慢、越抗暴力破解）。
	 *
	 * @param plainText 明文口令
	 * @param cost      4～31 之间的对数成本
 *
 * @author leiyangjun
 */
	public static String encode(String plainText, int cost) {
		if (plainText == null) {
			throw BizException.validWarn(AuthServerBizCode.PLAIN_TEXT_REQUIRED);
		}
		return BCrypt.hashpw(plainText, BCrypt.gensalt(cost));
	}

	/**
	 * 校验明文是否与已存储的 BCrypt 摘要匹配。
	 *
	 * @param plainText   待校验明文
	 * @param encodedHash 库中或其它来源的 BCrypt 摘要
	 * @return 是否匹配
 *
 * @author leiyangjun
 */
	public static boolean matches(String plainText, String encodedHash) {
		if (plainText == null || encodedHash == null) {
			return false;
		}
		return BCrypt.checkpw(plainText, encodedHash);
	}
}
