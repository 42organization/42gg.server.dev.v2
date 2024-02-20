package gg.pingpong.api.global.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.data.user.User;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.service.UserFindService;
import com.gg.server.global.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

	private final UserFindService userFindService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
		User user = userFindService.findByIntraId(username);
		return UserPrincipal.create(user);
	}

	public UserDetails loadUserById(Long id) {
		User user = userFindService.findUserById(id);
		return UserPrincipal.create(user);
	}
}
