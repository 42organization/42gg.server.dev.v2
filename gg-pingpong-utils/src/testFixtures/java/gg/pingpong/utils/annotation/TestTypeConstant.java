package gg.pingpong.utils.annotation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * TestType.
 *
 * <p>
 *  Tag 에서 사용할 테스트 타입을 정의
 * </p>
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestTypeConstant {
	public static final String UNIT_TEST = "UnitTest";
	public static final String INTEGRATION_TEST = "IntegrationTest";
}
