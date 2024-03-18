package gg.recruit.api.user.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitments;
import gg.recruit.api.user.service.response.RecruitmentDetailSvcDto;
import gg.recruit.api.user.service.response.RecruitmentListSvcDto;
import gg.repo.recruit.user.recruitment.RecruitmentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentService {
	private final RecruitmentRepository recruitmentRepository;
	private final QuestionService questionService;

	public RecruitmentListSvcDto findActiveRecruitmentList(Pageable pageable) {
		Page<Recruitments> pages = recruitmentRepository.findActiveRecruitmentList(LocalDateTime.now(), pageable);
		return new RecruitmentListSvcDto(pages.getContent(), pages.getTotalPages());
	}

	public RecruitmentDetailSvcDto findRecruitmentDetail(Long recruitmentId) {
		Recruitments recruit = recruitmentRepository.findById(recruitmentId).orElseThrow();
		List<Question> questions = questionService.findQuestionsByRecruitId(recruitmentId);
		return new RecruitmentDetailSvcDto(recruit, questions);
	}
}
