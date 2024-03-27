package gg.recruit.api.admin.controller.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.recruit.recruitment.Recruitment;
import gg.recruit.api.admin.service.response.Form;
import lombok.Getter;

@Getter
public class RecruitmentCreateReqDto {
	@FutureOrPresent(message = "시작일은 현재 시간 이후여야 합니다.")
	LocalDateTime startDateTime;

	@FutureOrPresent(message = "종료일은 현재 시간 이후여야 합니다.")
	LocalDateTime endDateTime;

	@NotBlank(message = "제목을 입력해주세요.")
	@Size(max = 255, message = "제목은 255자 이내로 입력해주세요.")
	String title;

	@NotBlank(message = "내용을 입력해주세요.")
	@Size(max = 3000, message = "내용은 3000자 이내로 입력해주세요.")
	String contents;

	@NotBlank(message = "모집 기수를 입력해주세요.")
	@Size(max = 50, message = "모집 세대는 50자 이내로 입력해주세요.")
	String generation;

	@NotNull @Valid
	List<Form> forms;

	public Recruitment toRecruitment() {
		return Recruitment.builder()
			.startTime(startDateTime)
			.endTime(endDateTime)
			.title(title)
			.contents(contents)
			.generation(generation)
			.build();
	}
}
