package gg.recruit.api.user.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gg.data.recruit.recruitment.Recruitments;
import gg.recruit.api.user.service.response.RecruitmentListSvcDto;
import gg.repo.recruit.user.recruitment.RecruitmentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentService {
	private final RecruitmentRepository recruitmentRepository;

	public RecruitmentListSvcDto findActiveRecruitmentList(Pageable pageable) {
		Page<Recruitments> pages = recruitmentRepository.findActiveRecruitmentList(LocalDateTime.now(), pageable);
		return new RecruitmentListSvcDto(pages.getContent(), pages.getTotalPages());
	}
}
