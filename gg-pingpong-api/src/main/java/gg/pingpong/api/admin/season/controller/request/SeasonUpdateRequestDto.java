package gg.pingpong.api.admin.season.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonUpdateRequestDto {
	@NotNull(message = "plz. seasonName")
	private String seasonName;

	@NotNull(message = "plz. startTime")
	@Future(message = "불가능한 예약시점입니다.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
	private LocalDateTime startTime;

	@NotNull(message = "plz. startPpp")
	private Integer startPpp;

	@NotNull(message = "plz. pppGap")
	private Integer pppGap;

}
