package org.peach.auth.security;

import org.apache.commons.lang3.StringUtils;
import org.peach.auth.entity.User;
import org.peach.auth.mapper.UserMapper;
import org.peach.common.security.UserType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {

	private final UserMapper userMapper;

	public AccountUserDetailsService(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (StringUtils.isBlank(username)) {
			throw new UsernameNotFoundException("用户名为空");
		}
		User probe = new User();
		probe.setUsername(username.trim());
		probe.setSubjectType(UserType.INTERNAL);
		probe.setValid("1");
		User user = this.userMapper.selectBaseOne(probe);
		if (user == null || StringUtils.isBlank(user.getPassword())) {
			throw new UsernameNotFoundException("用户不存在或未设置密码");
		}
		return new AccountDetails(user);
	}
}
