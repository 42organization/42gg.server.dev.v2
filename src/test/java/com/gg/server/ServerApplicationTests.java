package com.gg.server;

import com.gg.server.utils.annotation.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;

@IntegrationTest
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
