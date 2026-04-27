package org.peach.auth.security;

import java.util.Collection;
import java.util.List;

import org.peach.auth.entity.User;
import org.peach.common.security.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AccountDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private final User user;

	public AccountDetails(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public long getUserId() {
		return user.getId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String role = UserType.INTERNAL.equals(user.getSubjectType()) ? "ROLE_INTERNAL" : "ROLE_CUSTOMER";
		return List.of(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return "1".equals(user.getValid());
	}
}
