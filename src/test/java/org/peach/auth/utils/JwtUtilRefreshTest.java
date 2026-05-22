package org.peach.auth.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mvc.vo.LoginUserVO;

/**
 * refresh 令牌签发与解析。
 *
 * @author leiyangjun
 */
class JwtUtilRefreshTest {

	@Test
	void signTokenPairAndParseRefresh() {
		LoginUserVO user = new LoginUserVO();
		user.setId(1L);
		user.setUsername("admin");
		user.setNickname("管理员");

		var pair = JwtUtil.signTokenPair(user, 3600, 604800);
		assertThat(pair.getAccessToken()).isNotBlank();
		assertThat(pair.getRefreshToken()).isNotBlank();

		LoginUserVO parsed = JwtUtil.parseRefreshToken(pair.getRefreshToken());
		assertThat(parsed.getUsername()).isEqualTo("admin");
	}

	@Test
	void accessTokenMustNotPassRefreshValidation() {
		LoginUserVO user = new LoginUserVO();
		user.setUsername("u1");
		var access = JwtUtil.signAccessToken(user, 60);
		assertThatThrownBy(() -> JwtUtil.parseRefreshToken(access.getAccessToken())).isInstanceOf(BizException.class);
	}

}
