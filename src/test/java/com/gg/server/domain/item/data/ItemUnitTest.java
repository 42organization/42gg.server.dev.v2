package com.gg.server.domain.item.data;

import static com.gg.server.utils.ReflectionUtilsForUnitTest.setFieldWithReflection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.gg.server.utils.ReflectionUtilsForUnitTest;
import com.gg.server.utils.annotation.UnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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