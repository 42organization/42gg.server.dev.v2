package com.gg.server.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTextColorCheckService {
    public static boolean check(String textColor) {
        if (textColor == null)
            return false;
        if (textColor.length() != 7)
            return false;
        if (textColor.charAt(0) != '#')
            return false;
        for (int i = 1; i < 7; i++) {
            char c = textColor.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')))
                return false;
        }
        return true;
    }
}
