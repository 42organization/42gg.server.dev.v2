package com.gg.server.domain.user.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTextColorCheckService {
	public static boolean check(String textColor) {
		if (textColor == null) {
			return false;
		}
		if (textColor.length() != 7) {
			return false;
		}
		if (textColor.charAt(0) != '#') {
			return false;
		}
		for (int i = 1; i < 7; i++) {
			char charTestColor = textColor.charAt(i);
			if (!((charTestColor >= '0' && charTestColor <= '9') || (charTestColor >= 'a' && charTestColor <= 'f') || (
				charTestColor >= 'A' && charTestColor <= 'F'))) {
				return false;
			}
		}
		return true;
	}
}
