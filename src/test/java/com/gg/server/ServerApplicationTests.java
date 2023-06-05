package com.gg.server;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.TimeZone;

@SpringBootTest
class ServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("TimeZone Test")
	public void TimeZoneTest() throws Exception {
		TimeZone tz = TimeZone.getDefault();

		Assertions.assertThat(tz.getID()).isEqualTo("Asia/Seoul");
	}
}
