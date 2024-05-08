package gg.recruit.api.user.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.recruit.api.user.service.response.RecruitmentDetailSvcDto;
import gg.recruit.api.user.service.response.RecruitmentListSvcDto;
import gg.repo.recruit.recruitment.RecruitmentRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentService {
	private final RecruitmentRepository recruitmentRepository;
	private final QuestionService questionService;

	public RecruitmentListSvcDto findActiveRecruitmentList(Pageable pageable) {
		Page<Recruitment> pages = recruitmentRepository.findActiveRecruitmentList(LocalDateTime.now(), pageable);
		return new RecruitmentListSvcDto(pages.getContent(), pages.getTotalPages());
	}

	public RecruitmentDetailSvcDto findRecruitmentDetail(Long recruitmentId) {
		Recruitment recruit = recruitmentRepository.findByActiveRecruit(recruitmentId, LocalDateTime.now())
			.orElseThrow(() -> new NotExistException("Recruitment id 가 유효하지 않습니다."));
		List<Question> questions = questionService.findQuestionsByRecruitId(recruitmentId);
		return new RecruitmentDetailSvcDto(recruit, questions);
	}
}
