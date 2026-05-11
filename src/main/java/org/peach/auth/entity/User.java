package org.peach.auth.entity;

import java.io.Serial;
import java.io.Serializable;

import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.LogicDelete;
import org.peach.common.mybatis.annotation.TableName;
import org.peach.common.mybatis.annotation.Unique;

import lombok.Data;

/**
 * 与表 {@code cmn_user} 映射的登录用户实体（字段以满足认证查询为主，可按需扩展）。
 * <p>
 * {@code valid} 与库中 SMALLINT 一致，逻辑删除注解按 starter 约定映射为「有效」条件。
 * </p>
 * <p>
 * 与工程内其它实体一致使用 {@link Data}，避免重复维护访问器。
 * </p>
 */
@Data
@TableName("cmn_user")
public class User implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/** 主键，雪花 ID */
	@ID
	private Long id;

	/** 用户类型：system / app，与表约束一致 */
	private String userType;

	/** 登录名；system 用户由库约束保证非空 */
	@Unique
	private String username;

	/** BCrypt 等密码摘要；短信登录用户可为空 */
	private String password;

	/** 昵称 */
	private String nickname;

	/** 真实姓名 */
	private String realName;

	/** 手机号；短信登录按此字段匹配 {@code cmn_user.mobile} */
	private String mobile;

	/** 邮箱 */
	private String email;

	/** 性别：0 未知，1 男，2 女 */
	private Short gender;

	/** 头像地址或对象存储键 */
	private String avatar;

	/** 证件类型编码，可空 */
	private String certType;

	/** 证件号码，可空 */
	private String certNo;

	@LogicDelete
	private String valid;

	/**
	 * 与 {@code valid}（SMALLINT）及 MyBatis 映射（可能为 String / Number）兼容，判定账号是否可用。
	 */
	public static boolean isValidActive(Object valid) {
		if (valid == null) {
			return false;
		}
		if (valid instanceof Number) {
			return ((Number) valid).intValue() == 1;
		}
		String s = String.valueOf(valid).trim();
		return "1".equals(s);
	}
}
