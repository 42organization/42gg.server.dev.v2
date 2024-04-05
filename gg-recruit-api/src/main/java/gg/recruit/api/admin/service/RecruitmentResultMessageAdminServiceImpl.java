package gg.recruit.api.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.recruit.manage.RecruitResultMessageRepository;
import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDto;
import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDtoMapper;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentResultMessageAdminServiceImpl implements RecruitmentResultMessageAdminService {
	private final RecruitResultMessageRepository recruitResultMessageRepository;

	/**
	 * 동일 타입의 이전 메시지를 disable 하고 현재 메시지를 저장.
	 * @param reqDto
	 */
	@Override
	@Transactional
	public void postResultMessage(RecruitmentResultMessageDto reqDto) {
		recruitResultMessageRepository.disablePreviousResultMessages(reqDto.getMessageType());
		recruitResultMessageRepository.save(RecruitmentResultMessageDtoMapper.INSTANCE.dtoToEntity(reqDto));
	}

	/**
	 * 지원 결과 메시지 양식 전체 조회
	 * @return List<ResultMessage>
	 */
	@Override
	public List<ResultMessage> getResultMessages() {
		return recruitResultMessageRepository.findAllOrderByIdDesc();
	}

	/**
	 * 지원 결과 메시지 양식 미리보기
	 * @return String
	 */
	@Override
	public String getResultMessagePreview(MessageType messageType) {
		return recruitResultMessageRepository.findActiveResultMessageByMessageType(messageType)
			.orElseThrow(() -> new NotExistException("지원 결과 메시지가 존재하지 않습니다."))
			.getContent();
	}
}
