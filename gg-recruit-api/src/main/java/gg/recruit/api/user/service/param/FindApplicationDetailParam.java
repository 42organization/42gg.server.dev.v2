package gg.recruit.api.user.service.param;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindApplicationDetailParam {
	private Long userId;
	private Long recruitId;
	private Long applicationId;
}
