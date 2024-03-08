package gg.recruit.api.user.service.response;

import java.util.List;

import gg.data.recruit.recruitment.Recruitments;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitmentListSvcDto {
	private List<RecruitmentSvcDto> recruitments;
	private Integer totalPage;

	public RecruitmentListSvcDto(List<Recruitments> recruitments, Integer totalPage) {
		this.recruitments = recruitments.stream().map(RecruitmentSvcDto::new).toList();
		this.totalPage = totalPage;
	}
}
