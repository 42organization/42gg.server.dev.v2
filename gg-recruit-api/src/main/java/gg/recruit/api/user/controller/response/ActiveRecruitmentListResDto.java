package gg.recruit.api.user.controller.response;

import java.util.List;

import gg.recruit.api.user.service.response.RecruitmentListSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ActiveRecruitmentListResDto {
	private List<RecruitmentDto> recruitments;
	private Integer totalPage;

	public ActiveRecruitmentListResDto(RecruitmentListSvcDto dto) {
		this.recruitments = dto.getRecruitments().stream().map(RecruitmentDto::new).toList();
		this.totalPage = dto.getTotalPage();
	}
}
