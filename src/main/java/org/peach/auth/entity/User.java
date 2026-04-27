package org.peach.auth.entity;

import java.io.Serial;
import java.io.Serializable;

import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.LogicDelete;
import org.peach.common.mybatis.annotation.TableName;

@TableName("cmn_user")
public class User implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@ID
	private Long id;
	private String subjectType;
	private String username;
	private String password;
	private String nickname;
	@LogicDelete
	private String valid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}
}
