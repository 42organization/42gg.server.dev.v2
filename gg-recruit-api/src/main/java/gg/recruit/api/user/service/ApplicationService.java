package gg.recruit.api.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.ApplicationAnswerCheckList;
import gg.data.recruit.application.ApplicationAnswerText;
import gg.data.recruit.application.RecruitStatus;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import gg.data.user.User;
import gg.data.user.type.SnsType;
import gg.recruit.api.user.service.param.DelApplicationParam;
import gg.recruit.api.user.service.param.FindApplicationDetailParam;
import gg.recruit.api.user.service.param.FindApplicationResultParam;
import gg.recruit.api.user.service.param.FormParam;
import gg.recruit.api.user.service.param.FormPatchParam;
import gg.recruit.api.user.service.param.RecruitApplyFormParam;
import gg.recruit.api.user.service.param.RecruitApplyParam;
import gg.recruit.api.user.service.response.ApplicationListSvcDto;
import gg.recruit.api.user.service.response.ApplicationResultSvcDto;
import gg.recruit.api.user.service.response.ApplicationWithAnswerSvcDto;
import gg.repo.recruit.application.ApplicationAnswerRepository;
import gg.repo.recruit.application.ApplicationRepository;
import gg.repo.recruit.application.RecruitStatusRepository;
import gg.repo.recruit.recruitment.CheckListRepository;
import gg.repo.recruit.recruitment.QuestionRepository;
import gg.repo.recruit.recruitment.RecruitmentRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.ForbiddenException;
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
	private final RecruitStatusRepository recruitStatusRepository;

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

	/**
	 * user 가 recruit 에 지원한다.
	 * @param param
	 * @return
	 */
	public Long recruitApply(RecruitApplyParam param) {
		// application 에 기존 지원서가 존재하는지 확인 isExist
		Optional<Application> application = applicationRepository
			.findByUserIdAndRecruitId(param.getUserId(), param.getRecruitId());
		if (application.isPresent()) {
			throw new DuplicationException("이미 지원한 공고입니다. applicationId = " + application.get().getId());
		}
		// application 생성
		User user = userRepository.findById(param.getUserId())
			.orElseThrow(() -> new NotExistException("user not found"));
		if (user.getSnsNotiOpt().equals(SnsType.NONE) || user.getSnsNotiOpt().equals(SnsType.EMAIL)) {
			user.updateTypes(user.getRacketType(), SnsType.BOTH);
		}
		Recruitment recruitments = recruitmentRepository.getById(param.getRecruitId());
		// recruit가 종료되었는지 확인
		if (recruitments.getEndTime().isBefore(LocalDateTime.now())) {
			throw new ForbiddenException("마감된 공고입니다.");
		}

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

	public ApplicationResultSvcDto findApplicationResult(FindApplicationResultParam param) {
		Optional<Application> optionalApplication = applicationRepository
			.findApplication(param.getApplicationId(), param.getUserId(), param.getRecruitmentId());
		ApplicationResultSvcDto result;
		if (optionalApplication.isEmpty()) {
			result = ApplicationResultSvcDto.nullResult();
		} else {
			Application application = optionalApplication.get();
			Optional<RecruitStatus> recruitStatusOptional = recruitStatusRepository.findByApplicationId(
				param.getApplicationId());
			result = recruitStatusOptional.map(
					recruitStatus -> ApplicationResultSvcDto.of(application.getRecruitTitle(),
						application.getStatus(), recruitStatus.getInterviewDate()))
				.orElseGet(
					() -> ApplicationResultSvcDto.of(application.getRecruitTitle(), application.getStatus(), null));
		}
		return result;
	}

	@Transactional
	public void updateApplication(FormPatchParam param) {
		Application application = applicationRepository.findByUserIdAndRecruitId(param.getUserId(),
			param.getRecruitmentId()).orElseThrow(() ->
			new NotExistException("application not found"));

		//delete all existing answers
		List<Long> questionIds = param.getQuestionIds();
		List<ApplicationAnswer> res = applicationAnswerRepository.findAllByQuestionIds(param.getUserId(),
			param.getApplicationId(), param.getRecruitmentId(), questionIds);
		applicationAnswerRepository.deleteAll(res);

		Map<Long, Question> questionMap = questionRepository.findAllById(questionIds)
			.stream()
			.collect(Collectors.toMap(Question::getId, q -> q));

		Map<Long, CheckList> checkListMap = checkListRepository.findAllByQuestionIds(questionIds)
			.stream()
			.collect(Collectors.toMap(CheckList::getId, q -> q));
		applicationAnswerRepository.saveAll(
			toApplicationAnswer(application, questionMap, checkListMap, param.getForms()));
	}

	private List<ApplicationAnswer> toApplicationAnswer(Application application, Map<Long, Question> questionMap,
		Map<Long, CheckList> checkListMap, List<FormParam> forms) {
		List<ApplicationAnswer> newAnswers = new ArrayList<>();
		forms.stream().forEach(form -> {
			newAnswers.addAll(form.toApplicationAnswer(application, questionMap, checkListMap));
		});
		return newAnswers;
	}

	@Transactional
	public void deleteApplication(DelApplicationParam param) {
		Application application = applicationRepository
			.findByUserIdAndRecruitId(param.getUserId(), param.getRecruitmentId())
			.orElseThrow(() -> new NotExistException("application not found"));
		if (!application.isUpdateAvailable()) {
			throw new ForbiddenException("지원서 수정 기간이 지났습니다.");
		}
		if (application.getId().equals(param.getApplicationId())) {
			application.delete();
		} else {
			throw new NotExistException("application not found");
		}
	}
}
