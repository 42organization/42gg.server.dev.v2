package com.gg.server.global.utils;

import java.util.Collection;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Checker.
 *
 * <p>
 *  비즈니스 로직 체크용 check 유틸리티 클래스
 * </p>
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BusinessChecker {
	public static void mustNotNull(Object object, ErrorCode errorCode) {
		if (object == null) {
			throw new BusinessException(errorCode);
		}
	}

	public static void mustContains(Object object, Collection<?> collection, ErrorCode errorCode) {
		if (!collection.contains(object)) {
			throw new BusinessException(errorCode);
		}
	}

	public static void mustNotContains(Object object, Collection<?> collection, ErrorCode errorCode) {
		if (collection.contains(object)) {
			throw new BusinessException(errorCode);
		}
	}

	public static void mustNotExceed(int size, Collection<?> collection, ErrorCode errorCode) {
		if (collection.size() > size) {
			throw new BusinessException(errorCode);
		}
	}
}
