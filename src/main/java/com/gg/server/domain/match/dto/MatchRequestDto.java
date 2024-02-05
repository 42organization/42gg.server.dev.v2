package com.gg.server.domain.match.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gg.server.data.match.type.Option;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRequestDto {

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime startTime;
	@NotNull
	@JsonProperty("mode")
	private Option option;

	public MatchRequestDto(LocalDateTime startTime, Option option) {
		this.startTime = startTime;
		this.option = option;
	}
}
