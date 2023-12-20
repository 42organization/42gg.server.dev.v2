package com.gg.server.global.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
