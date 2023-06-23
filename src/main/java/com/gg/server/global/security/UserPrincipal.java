package com.gg.server.global.security;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.type.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
public class UserPrincipal implements OAuth2User, UserDetails {
    private final Long id;
    private final String nickname;
    private final String profileImg;
    private final Collection<GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id, String nickname, String profileImg, Collection<GrantedAuthority> authorities) {
        this.id = id;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRoleType().getKey().equals("USER")) {
            authorities.add(new SimpleGrantedAuthority(RoleType.USER.getKey()));
        } else if (user.getRoleType().getKey().equals("ADMIN")) {
            authorities.add(new SimpleGrantedAuthority(RoleType.USER.getKey()));
            authorities.add(new SimpleGrantedAuthority(RoleType.ADMIN.getKey()));
        }
        return new UserPrincipal(user.getId(), user.getIntraId(), user.getImageUri(), authorities);
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