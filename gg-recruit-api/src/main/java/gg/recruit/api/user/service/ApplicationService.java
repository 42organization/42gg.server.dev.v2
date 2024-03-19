package gg.recruit.api.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.ApplicationAnswerCheckList;
import gg.data.recruit.application.ApplicationAnswerText;
import gg.data.recruit.recruitment.Recruitments;
import gg.data.recruit.recruitment.enums.InputType;
import gg.data.user.User;
import gg.recruit.api.user.service.param.FindApplicationDetailParam;
import gg.recruit.api.user.service.param.RecruitApplyFormParam;
import gg.recruit.api.user.service.param.RecruitApplyParam;
import gg.recruit.api.user.service.response.ApplicationListSvcDto;
import gg.recruit.api.user.service.response.ApplicationWithAnswerSvcDto;
import gg.repo.recruit.user.application.ApplicationAnswerRepository;
import gg.repo.recruit.user.application.ApplicationRepository;
import gg.repo.recruit.user.recruitment.CheckListRepository;
import gg.repo.recruit.user.recruitment.QuestionRepository;
import gg.repo.recruit.user.recruitment.RecruitmentRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final ApplicationAnswerRepository applicationAnswerRepository;
	private final UserRepository userRepository;
	private final RecruitmentRepository recruitmentRepository;
	private final QuestionRepository questionRepository;
	private final CheckListRepository checkListRepository;

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

	public Long recruitApply(RecruitApplyParam param) {
		// application 에 기존 지원서가 존재하는지 확인 isExist
		Optional<Application> application = applicationRepository
			.findByUserIdAndRecruitId(param.getUserId(), param.getRecruitId());
		if (application.isPresent()) {
			throw new DuplicationException("이미 지원한 공고입니다. applicationId = " + application.get().getId());
		}
		// application 생성
		User user = userRepository.getById(param.getUserId());
		Recruitments recruitments = recruitmentRepository.getById(param.getRecruitId());
		Application newApplication = applicationRepository.save(new Application(user, recruitments));
		for (RecruitApplyFormParam form :
			param.getForms()) {
			if (form.getInputType().equals(InputType.TEXT)) {
				applicationAnswerRepository.save(
					new ApplicationAnswerText(newApplication, questionRepository.getById(form.getQuestionId()),
						form.getAnswer()));
			} else {
				for (Long checkedId : form.getCheckedList()) {
					applicationAnswerRepository.save(
						new ApplicationAnswerCheckList(newApplication, questionRepository.getById(form.getQuestionId()),
							checkListRepository.getById(checkedId)));
				}
			}
		}
		return newApplication.getId();
	}
}
