package gg.pingpong.api.admin.manage.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.admin.repo.feedback.FeedbackAdminRepository;
import gg.pingpong.api.admin.manage.controller.response.FeedbackAdminResponseDto;
import gg.pingpong.api.admin.manage.controller.response.FeedbackListAdminResponseDto;
import gg.pingpong.data.manage.Feedback;
import gg.pingpong.utils.exception.feedback.FeedbackNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackAdminService {
	private final FeedbackAdminRepository feedbackAdminRepository;

	/**
	 * <p>모든 피드백을 페이지네이션으로 만들어서 반환하는 메서드입니다</p>
	 * @param pageable
	 * @return FeedbackListAdminResponseDto
	 */
	@Transactional(readOnly = true)
	public FeedbackListAdminResponseDto findAllFeedback(Pageable pageable) {
		Page<Feedback> feedbacks = feedbackAdminRepository.findAll(pageable);
		Page<FeedbackAdminResponseDto> feedbackAdminResponseDto = feedbacks.map(FeedbackAdminResponseDto::new);

		return new FeedbackListAdminResponseDto(
			feedbackAdminResponseDto.getContent(),
			feedbackAdminResponseDto.getTotalPages());
	}

	/**
	 * <p>피드백 해결상황을 변경해주는 메서드입니다.</p>
	 * @param feedbackId 타겟 피드백 id
	 */
	@Transactional
	public void toggleFeedbackIsSolvedByAdmin(Long feedbackId) {
		Feedback feedback = feedbackAdminRepository.findById(feedbackId).orElseThrow(FeedbackNotFoundException::new);
		feedback.setIsSolved(!feedback.getIsSolved());
	}

	/**
	 * <p>타겟 유저의 피드백을 페이지네이션으로 만들어서 반환하는 메서드입니다.</p>
	 * @param intraId 타겟 유저 intraId
	 * @param pageable
	 * @return FeedbackListAdminResponseDto
	 */
	@Transactional(readOnly = true)
	public FeedbackListAdminResponseDto findByPartsOfIntraId(String intraId, Pageable pageable) {
		List<Feedback> feedbackList = feedbackAdminRepository.findFeedbacksByUserIntraId(intraId);
		Page<Feedback> feedbacks = new PageImpl<>(feedbackList, pageable, feedbackList.size());
		Page<FeedbackAdminResponseDto> feedbackAdminResponseDto = feedbacks.map(FeedbackAdminResponseDto::new);
		return new FeedbackListAdminResponseDto(
			feedbackAdminResponseDto.getContent(),
			feedbackAdminResponseDto.getTotalPages());
	}
}
