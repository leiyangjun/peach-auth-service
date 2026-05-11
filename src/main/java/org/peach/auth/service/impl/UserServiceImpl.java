package org.peach.auth.service.impl;

import org.peach.auth.dto.LoginPasswordDTO;
import org.peach.auth.dto.TokenDTO;
import org.peach.auth.entity.User;
import org.peach.auth.mapper.UserMapper;
import org.peach.auth.service.SliderCaptchaService;
import org.peach.auth.service.UserService;
import org.peach.auth.utils.BCryptUtil;
import org.peach.auth.utils.JwtUtil;
import org.peach.auth.code.AuthServerBizCode;
import org.peach.auth.utils.RSAUtil;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mvc.vo.LoginUserVO;
import org.peach.common.utils.BeanUtil;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 用户业务实现：滑块 → RSA 解密口令 → BCrypt 比对 → 签发 JWT。
 */
@Service
public class UserServiceImpl extends BaseAbstractService<UserMapper, User, User> implements UserService {

	private final SliderCaptchaService sliderCaptchaService;

	/** 访问令牌有效期（秒），未配置时默认 3600；可通过 {@code peach.jwt.expires-in} 覆盖。 */
	@Value("${peach.jwt.expires-in:3600}")
	private long jwtExpiresInSeconds;

	public UserServiceImpl(UserMapper userMapper, SliderCaptchaService sliderCaptchaService) {
		super(userMapper, User.class, User.class);
		this.sliderCaptchaService = sliderCaptchaService;
	}

	@Override
	public TokenDTO loginByPassword(LoginPasswordDTO dto) {
		if (!this.sliderCaptchaService.verifyAndConsume(dto.getCaptchaId(), dto.getSliderOffset())) {
			throw BizException.badRequest(AuthServerBizCode.SLIDER_VERIFY_FAILED);
		}
		// 解密拿到原始明文密码
		String plainPassword = RSAUtil.decrypt(dto.getPassword());

		User user = this.mapper.selectUniqueValid(dto.getUsername(), User.class);
		if (user == null) {
			throw BizException.badRequest(AuthServerBizCode.UN_VALID_USER);
		}
		if (!BCryptUtil.matches(plainPassword, user.getPassword())) {
			throw BizException.badRequest(AuthServerBizCode.LOGIN_BAD_CREDENTIALS);
		}
		return JwtUtil.signAccessToken(BeanUtil.copy(user, LoginUserVO.class), this.jwtExpiresInSeconds);
	}
}
