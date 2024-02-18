package com.gg.server.data.manage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class PenaltyTest {

	@Test
	void updateStartTime() {
		// given
		Penalty penalty = new Penalty();
		// when
		penalty.updateStartTime(LocalDateTime.now());
		// then
		assertNotNull(penalty.getStartTime());
	}
}
