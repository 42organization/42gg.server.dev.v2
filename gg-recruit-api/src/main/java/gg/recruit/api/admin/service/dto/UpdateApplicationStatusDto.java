package gg.recruit.api.admin.service.dto;

import gg.data.recruit.application.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateApplicationStatusDto {
	ApplicationStatus status;
	Long applicationId;
	Long recruitId;
}
