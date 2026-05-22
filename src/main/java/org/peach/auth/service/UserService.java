package org.peach.auth.service;

import org.peach.auth.dto.LoginPasswordDTO;
import org.peach.auth.dto.TokenDTO;
import org.peach.auth.entity.User;
import org.peach.common.mybatis.service.BaseInterfaceService;

/**
 * 用户业务：通用 CRUD（见 {@link BaseInterfaceService}）与登录等。
 */
public interface UserService extends BaseInterfaceService<User> {

	/**
	 * 用户名密码登录（DTO 须含 RSA 密文口令与滑块参数）。
	 *
	 * @throws org.peach.common.mvc.exception.BizException 滑块失败、密文解密失败、用户名或口令错误等
 *
 * @author leiyangjun
 */
	TokenDTO loginByPassword(LoginPasswordDTO dto);
}
