package com.gg.server.data.game;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
public class PChangeDataTest {

	@Test
	public void checkPChangeTest() {

		//given
		PChange pChange = new PChange();

		//when
		pChange.checkPChange();

		//then
		Assertions.assertThat(pChange.getIsChecked()).isEqualTo(true);
	}

	@Test
	public void updatePPPTest() {

		//given
		PChange pChange = new PChange();
		int testPPP = 4242;

		//when
		pChange.updatePPP(testPPP);

		//then
		Assertions.assertThat(pChange.getPppResult()).isEqualTo(testPPP);
	}
}
