package com.gg.server.global.security.cookie;


import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

public class CookieUtil {
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

//    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, String domain) {
//        ResponseCookie cookie = ResponseCookie.from(name, value)
//                .maxAge(maxAge)
////                .domain(domain)
////                .httpOnly(true)
//                .path("/")
//                .secure(true)
//                .sameSite(SameSite.NONE.attributeValue())
//                .build();
//
//        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, String domain) {
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(name).append("=").append(value).append("; ");
        cookieBuilder.append("Max-Age=").append(maxAge).append("; ");
        cookieBuilder.append("Domain=").append(domain).append("; ");
        cookieBuilder.append("Path=/; ");
        cookieBuilder.append("Secure; ");
        cookieBuilder.append("SameSite=None");

        response.setHeader("Set-Cookie", cookieBuilder.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}

