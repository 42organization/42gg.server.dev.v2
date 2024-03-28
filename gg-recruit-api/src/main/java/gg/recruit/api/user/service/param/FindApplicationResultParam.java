package gg.recruit.api.user.service.param;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindApplicationResultParam {

	private Long userId;
	private Long recruitmentId;
	private Long applicationId;

}
