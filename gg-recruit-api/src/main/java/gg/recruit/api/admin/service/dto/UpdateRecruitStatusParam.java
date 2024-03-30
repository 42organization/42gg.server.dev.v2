package gg.recruit.api.admin.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateRecruitStatusParam {
	private Long recruitId;
	private Boolean finish;
}
