package gg.recruit.api.admin.controller.request;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import gg.data.recruit.application.enums.ApplicationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SetFinalApplicationStatusResultReqDto {
	public static final String MUST_FINAL_STATUS = "최종 결과는 PASS 혹은 FAIL 이어야 합니다!";

	@NotNull
	ApplicationStatus status;

	@AssertTrue(message = MUST_FINAL_STATUS)
	private boolean isValidFinalStatus() {
		return status != null && status.isFinal;
	}
}
