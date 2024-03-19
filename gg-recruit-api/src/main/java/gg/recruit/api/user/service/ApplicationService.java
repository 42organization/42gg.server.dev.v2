package gg.recruit.api.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.recruit.api.user.service.param.FindApplicationDetailParam;
import gg.recruit.api.user.service.response.ApplicationListSvcDto;
import gg.recruit.api.user.service.response.ApplicationWithAnswerSvcDto;
import gg.repo.recruit.user.application.ApplicationAnswerRepository;
import gg.repo.recruit.user.application.ApplicationRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final ApplicationAnswerRepository applicationAnswerRepository;

	public ApplicationListSvcDto findMyApplications(Long userId) {
		List<Application> res = applicationRepository.findAllByUserId(userId);
		return new ApplicationListSvcDto(res);
	}

	public ApplicationWithAnswerSvcDto findMyApplicationDetail(FindApplicationDetailParam param) {
		List<ApplicationAnswer> answers = applicationAnswerRepository
			.findAllAnswers(param.getUserId(), param.getRecruitId(), param.getApplicationId());
		if (answers.size() == 0) {
			throw new NotExistException("application not found", ErrorCode.BAD_REQUEST);
		}
		return new ApplicationWithAnswerSvcDto(answers);
	}

	public Long findApplicationByUserAndRecruit(Long userId, Long recruitId) {
		Optional<Application> application = applicationRepository.findByUserIdAndRecruitId(userId, recruitId);
		return application.map(Application::getId).orElse(null);
	}
}
