package com.gg.server.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * ConstantConfig.
 *
 * <p>
 *  yml 파일에 정의된 변수들을 가져오는 클래스.
 * </p>
 *
 */
@Component
@Getter
public class ConstantConfig {
	@Value("${constant.allowedMinimalStartDays}")
	private int allowedMinimalStartDays;

	@Value("${constant.tournamentSchedule}")
	private String tournamentSchedule;
}
