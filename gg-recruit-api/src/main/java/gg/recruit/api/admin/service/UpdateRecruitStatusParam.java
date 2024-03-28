package gg.recruit.api.admin.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateRecruitStatusParam {
	private Long recruitId;
	private Boolean finish;
}
