package gg.pingpong.api.data.store;

import static gg.pingpong.api.utils.ReflectionUtilsForUnitTest.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.pingpong.data.store.Item;
import gg.pingpong.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class ItemUnitTest {
	@Nested
	@DisplayName("imageUpdateTest")
	class ImageUpdateTest {
		@Test
		@DisplayName("success")
		void success() {
			Item item = new Item();
			String after = "after";
			setFieldWithReflection(item, "imageUri", "before");
			item.imageUpdate(after);
			assertThat(item.getImageUri()).isEqualTo(after);
		}
	}

	@Nested
	@DisplayName("setVisibilityTest")
	class SetVisibilityTest {
		@Test
		@DisplayName("success")
		void success() {
			Item item = new Item();
			String intraId = "intraId";
			setFieldWithReflection(item, "isVisible", true);
			item.setVisibility(intraId);
			assertThat(item.getIsVisible()).isFalse();
			assertThat(item.getDeleterIntraId()).isEqualTo(intraId);
		}
	}

}
