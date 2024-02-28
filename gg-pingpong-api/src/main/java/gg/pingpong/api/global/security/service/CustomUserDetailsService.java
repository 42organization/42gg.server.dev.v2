package gg.pingpong.api.global.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.global.security.UserPrincipal;
import gg.pingpong.api.user.user.service.UserFindService;
import gg.pingpong.data.user.User;
import gg.pingpong.utils.exception.user.UserNotFoundException;
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
