package gg.pingpong.api.user.user.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTextColorCheckService {

	/**
	 * textColor 의 유효성 검사
	 * @param textColor
	 * @return boolean
	 */
	public static boolean check(String textColor) {
		return textColor != null && textColor.matches("#[0-9a-fA-F]{6}");
	}
}
