package com.gg.server.global.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.gg.server.data.user.User;
import com.gg.server.data.user.type.RoleType;

import lombok.Getter;

@Getter
public class UserPrincipal implements OAuth2User, UserDetails {
	private final Long id;
	private final String nickname;
	private final Collection<GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	public UserPrincipal(Long id, String nickname, Collection<GrantedAuthority> authorities) {
		this.id = id;
		this.nickname = nickname;
		this.authorities = authorities;
	}

	public static UserPrincipal create(User user) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (user.getRoleType().getKey().equals("ROLE_USER")) {
			authorities.add(new SimpleGrantedAuthority(RoleType.USER.getKey()));
		} else if (user.getRoleType().getKey().equals("ROLE_ADMIN")) {
			authorities.add(new SimpleGrantedAuthority(RoleType.USER.getKey()));
			authorities.add(new SimpleGrantedAuthority(RoleType.ADMIN.getKey()));
		}
		return new UserPrincipal(user.getId(), user.getIntraId(), authorities);
	}

	public static UserPrincipal create(User user, Map<String, Object> attributes) {
		UserPrincipal userPrincipal = UserPrincipal.create(user);
		userPrincipal.setAttributes(attributes);
		return userPrincipal;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return nickname;
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
		return true;
	}

	@Override
	public String getName() {
		return String.valueOf(id);
	}
}
