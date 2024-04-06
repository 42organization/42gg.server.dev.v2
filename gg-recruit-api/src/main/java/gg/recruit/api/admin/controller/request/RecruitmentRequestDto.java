package gg.recruit.api.admin.controller.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.recruit.recruitment.Recruitment;
import gg.recruit.api.admin.service.param.FormParam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RecruitmentRequestDto {
	@NotNull(message = "시작일을 입력해주세요.")
	@FutureOrPresent(message = "시작일은 현재 시간 이후여야 합니다.")
	LocalDateTime startDate;

	@NotNull(message = "종료일을 입력해주세요.")
	@FutureOrPresent(message = "종료일은 현재 시간 이후여야 합니다.")
	LocalDateTime endDate;

	@NotBlank(message = "제목을 입력해주세요.")
	@Size(max = 255, message = "제목은 255자 이내로 입력해주세요.")
	String title;

	@NotBlank(message = "내용을 입력해주세요.")
	@Size(max = 3000, message = "내용은 3000자 이내로 입력해주세요.")
	String contents;

	@NotBlank(message = "모집 기수를 입력해주세요.")
	@Size(max = 50, message = "모집 세대는 50자 이내로 입력해주세요.")
	String generation;

	@NotNull(message = "폼을 입력해주세요.")
	@Valid
	List<FormParam> form;

	@Builder
	public RecruitmentRequestDto(LocalDateTime startDate, LocalDateTime endDate, String title,
		String contents,
		String generation, List<FormParam> form) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.title = title;
		this.contents = contents;
		this.generation = generation;
		this.form = form;
	}

	@Mapper
	public interface RecruitmentMapper {
		RecruitmentMapper INSTANCE = Mappers.getMapper(RecruitmentMapper.class);

		@Mapping(source = "startDate", target = "startTime")
		@Mapping(source = "endDate", target = "endTime")
		Recruitment dtoToEntity(RecruitmentRequestDto dto);
	}
}
