package gg.calendar.api.user.schedule.privateschedule.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateScheduleUpdateReqDto {
	@NotBlank
	@Size(max = 50)
	private String title;

	@Size(max = 2000)
	private String content;

	@Size(max = 255, message = "링크는 255자 이하로 입력해주세요.")
	private String link;

	@NotNull
	private LocalDateTime startTime;

	@NotNull
	private LocalDateTime endTime;

	@NotNull
	private boolean alarm;

	@NotNull
	private Long groupId;

	@Builder
	private PrivateScheduleUpdateReqDto(String title, String content, String link, LocalDateTime startTime,
		LocalDateTime endTime, boolean alarm, Long groupId) {
		this.title = title;
		this.content = content;
		this.link = link;
		this.startTime = startTime;
		this.endTime = endTime;
		this.alarm = alarm;
		this.groupId = groupId;
	}
}
