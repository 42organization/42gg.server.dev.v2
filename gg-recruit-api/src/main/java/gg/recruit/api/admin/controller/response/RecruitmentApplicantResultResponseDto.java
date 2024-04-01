package gg.recruit.api.admin.controller.response;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.enums.ApplicationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecruitmentApplicantResultResponseDto {
	private Long applicationId;
	private String intraId;
	private LocalDateTime interviewDate;
	private ApplicationStatus result;

	public RecruitmentApplicantResultResponseDto(Long applicationId, String intraId, LocalDateTime interviewDate,
		ApplicationStatus result) {
		this.applicationId = applicationId;
		this.intraId = intraId;
		this.interviewDate = interviewDate;
		this.result = result;
	}

	@Mapper
	public interface MapStruct {
		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		@Mapping(source = "id", target = "applicationId")
		@Mapping(source = "user.intraId", target = "intraId")
		@Mapping(target = "interviewDate", expression = "java(mapInterviewDate(dto))")
		@Mapping(source = "status", target = "result")
		RecruitmentApplicantResultResponseDto entityToDto(Application dto);

		default LocalDateTime mapInterviewDate(Application application) {
			if (application.getRecruitStatus() != null) {
				return application.getRecruitStatus().getInterviewDate();
			} else {
				return null;
			}
		}
	}
}
