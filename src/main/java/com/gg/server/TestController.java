package com.gg.server;

import com.gg.server.global.security.cookie.CookieUtil;
import com.gg.server.global.security.jwt.utils.TokenHeaders;
import com.gg.server.global.utils.ApplicationYmlRead;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final ApplicationYmlRead applicationYmlRead;
    @GetMapping("/user")
    public String testUser() {
        return "user check ok";
    }

    @GetMapping("/admin")
    public String testAdmin() {
        return "admin check ok";
    }

    @GetMapping("/test/kakao")
    public void testConnectOauth2Kakao(HttpServletResponse response,
            @RequestParam(defaultValue = "test") String accessToken) throws IOException {
        CookieUtil.addCookie(response, TokenHeaders.ACCESS_TOKEN, accessToken, 1000000, "localhost");
        response.sendRedirect(applicationYmlRead.getFrontUrl() + "/oauth2/authorization/kakao");
    }

    @GetMapping("/test/42")
    public void testConnectOauth2FortyTwo(HttpServletResponse response,
                                       @RequestParam(defaultValue = "test") String accessToken) throws IOException {
        CookieUtil.addCookie(response, TokenHeaders.ACCESS_TOKEN, accessToken, 1000000, "localhost");
        response.sendRedirect(applicationYmlRead.getFrontUrl() + "/oauth2/authorization/42");
    }
}
