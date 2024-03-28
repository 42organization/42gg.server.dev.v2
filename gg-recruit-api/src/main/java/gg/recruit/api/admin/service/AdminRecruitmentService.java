package gg.recruit.api.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.recruit.recruitment.Recruitments;
import gg.repo.recruit.user.recruitment.RecruitmentRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminRecruitmentService {
	private final RecruitmentRepository recruitmentRepository;

	@Transactional
	public void updateRecruitStatus(UpdateRecruitStatusParam updateRecruitStatusParam) {
		Recruitments recruitments = recruitmentRepository.findById(updateRecruitStatusParam.getRecruitId())
			.orElseThrow(() -> new NotExistException("Recruitment not found."));
		recruitments.setFinish(updateRecruitStatusParam.getFinish());
	}
}
