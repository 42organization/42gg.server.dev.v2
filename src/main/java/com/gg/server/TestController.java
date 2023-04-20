package com.gg.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/user")
    public String testUser() {
        return "user check ok";
    }

    @GetMapping("/admin")
    public String testAdmin() {
        return "admin check ok";
    }
}
