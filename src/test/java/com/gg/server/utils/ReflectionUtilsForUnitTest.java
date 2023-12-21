package com.gg.server.utils;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;
import java.lang.reflect.Field;

/**
 * ReflectionUtilsForUnitTest.
 *
 * <p>
 *  유닛 테스트를 위한 리플렉션 유틸리티
 * </p>
 *
 */
public class ReflectionUtilsForUnitTest {

  /**
   * 리플렉션을 사용해서 필드값을 설정한다.
   */
  static public void setFieldWithReflection(Object object, String fieldName, Object value) {
    try {
      Field field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(object, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }
}
