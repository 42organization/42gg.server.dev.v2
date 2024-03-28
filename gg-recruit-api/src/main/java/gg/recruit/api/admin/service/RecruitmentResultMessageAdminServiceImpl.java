package gg.recruit.api.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDto;
import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDtoMapper;
import gg.repo.recruit.user.manage.RecruitResultMessageRepository;
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
}
