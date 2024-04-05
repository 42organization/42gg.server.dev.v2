package gg.recruit.api.admin.service;

import java.util.List;

import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
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

	/**
	 * 지원 결과 메시지 목록 조회
	 * @return List<ResultMessage>
	 */
	List<ResultMessage> getResultMessages();

	String getResultMessagePreview(MessageType messageType);
}
