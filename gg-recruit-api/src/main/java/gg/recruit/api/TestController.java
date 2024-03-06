package gg.recruit.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;

@RestController
public class TestController {

	@GetMapping("/test")
	public String test() {
		return "test";
	}

	@GetMapping("/login")
	public String login(@Login UserDto user) {
		System.out.println("user = " + user);
		return "login";
	}
}
