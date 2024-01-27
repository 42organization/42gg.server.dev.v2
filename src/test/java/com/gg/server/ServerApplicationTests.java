package com.gg.server;

import java.util.TimeZone;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gg.server.utils.annotation.IntegrationTest;

@IntegrationTest
class ServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("TimeZone Test")
	public void timeZoneTest() throws Exception {
		TimeZone tz = TimeZone.getDefault();

		Assertions.assertThat(tz.getID()).isEqualTo("Asia/Seoul");
	}
}
