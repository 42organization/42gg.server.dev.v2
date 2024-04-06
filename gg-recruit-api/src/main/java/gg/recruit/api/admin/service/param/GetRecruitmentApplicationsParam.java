package gg.recruit.api.admin.service.param;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GetRecruitmentApplicationsParam {
	private Long recruitId;
	private Long questionId;
	private List<Long> checkListIds;
	private String search;
	private Pageable pageable;

	public GetRecruitmentApplicationsParam(Long recruitId, Long questionId, List<Long> checkListIds, String search,
		Pageable pageable) {
		this.recruitId = recruitId;
		this.questionId = questionId;
		this.checkListIds = checkListIds;
		this.search = search;
		this.pageable = pageable;
	}
}
