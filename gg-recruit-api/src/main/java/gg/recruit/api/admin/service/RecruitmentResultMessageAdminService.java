package gg.recruit.api.admin.service;

import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDto;

/**
 * RecruitmentResultMessageAdminService.
 *
 * <p>
 *
 * </p>
 * @see             :
 * @author          : middlefitting
 * @since           : 2024/03/20
 */
public interface RecruitmentResultMessageAdminService {
	/**
	 * 지원 결과 메시지 등록
	 * @param reqDto
	 */
	void postResultMessage(RecruitmentResultMessageDto reqDto);
}
