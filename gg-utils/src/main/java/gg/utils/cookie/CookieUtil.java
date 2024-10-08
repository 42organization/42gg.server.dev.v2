package gg.utils.cookie;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import gg.utils.ApplicationYmlRead;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CookieUtil {

	private final ApplicationYmlRead applicationYmlRead;

	public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
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

	public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		String domain = applicationYmlRead.getDomain();
		ResponseCookie cookie = ResponseCookie.from(name, value)
			.maxAge(maxAge)
			.domain(domain)
			.httpOnly(false)
			.path("/")
			.secure(true)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	public void deleteCookie(HttpServletResponse response, String name) {
		String domain = applicationYmlRead.getDomain();
		ResponseCookie cookie = ResponseCookie.from(name, null)
			.maxAge(0)
			.domain(domain)
			.httpOnly(false)
			.path("/")
			.secure(true)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
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

	public String serialize(Object obj) {
		return Base64.getUrlEncoder()
			.encodeToString(SerializationUtils.serialize(obj));
	}

	public <T> T deserialize(Cookie cookie, Class<T> cls) {
		return cls.cast(
			SerializationUtils.deserialize(
				Base64.getUrlDecoder().decode(cookie.getValue())
			)
		);
	}
}

