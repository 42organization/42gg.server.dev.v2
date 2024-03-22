package gg.recruit.api.user;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.ApplicationAnswerCheckList;
import gg.data.recruit.application.ApplicationAnswerText;
import gg.data.recruit.application.RecruitStatus;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitments;
import gg.data.recruit.recruitment.enums.InputType;
import gg.data.user.User;
import gg.repo.recruit.user.application.ApplicationAnswerRepository;
import gg.repo.recruit.user.application.ApplicationRepository;
import gg.repo.recruit.user.application.RecruitStatusRepository;
import gg.repo.recruit.user.recruitment.CheckListRepository;
import gg.repo.recruit.user.recruitment.QuestionRepository;
import gg.repo.recruit.user.recruitment.RecruitmentRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecruitMockData {
	private final RecruitmentRepository recruitmentRepository;
	private final ApplicationRepository applicationRepository;
	private final QuestionRepository questionRepository;
	private final RecruitStatusRepository recruitStatusRepository;
	private final CheckListRepository checkListRepository;
	private final ApplicationAnswerRepository applicationAnswerRepository;

	public Recruitments createRecruitments() {
		Recruitments recruitments = new Recruitments("title", "contents", "generation",
			LocalDateTime.now(), LocalDateTime.now().plusDays(1));
		return recruitmentRepository.save(recruitments);
	}

	public Recruitments createRecruitmentsDel() {
		Recruitments recruitments = new Recruitments("title", "contents", "generation",
			LocalDateTime.now(), LocalDateTime.now().plusDays(1));
		recruitments.del();
		return recruitmentRepository.save(recruitments);
	}

	public Recruitments createRecruitmentsEnd() {
		Recruitments recruitments = new Recruitments("title", "contents", "generation",
			LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
		return recruitmentRepository.save(recruitments);
	}

	public Application createApplication(User user, Recruitments recruitments) {
		Application application = new Application(user, recruitments);
		return applicationRepository.save(application);
	}

	public Question createQuestion(Recruitments recruitments) {
		Question question = new Question(recruitments, InputType.TEXT, "question", 1);
		return questionRepository.save(question);
	}

	public Question createQuestion(Recruitments recruitments, String question, InputType inputType, String... checkList){
		Question questionEntity = new Question(recruitments, inputType, question, 1);
		questionRepository.save(questionEntity);
		for (String check : checkList) {
			CheckList checkListEntity = new CheckList(questionEntity, check);
			checkListRepository.save(checkListEntity);
		}
		return questionEntity;
	}

	public RecruitStatus createRecruitStatus(Application application) {
		RecruitStatus recruitStatus = new RecruitStatus(application);
		return recruitStatusRepository.save(recruitStatus);
	}

	public ApplicationAnswer makeAnswer(Application application, Question question, String content) {
		ApplicationAnswer answer = new ApplicationAnswerText(application, question, content);
		return applicationAnswerRepository.save(answer);
	}

	public void makeAnswer(Application application, Question question, Long checkedId) {
		CheckList checkList = checkListRepository.findById(checkedId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 체크리스트"));
		ApplicationAnswer answer = new ApplicationAnswerCheckList(application, question, checkList);
		applicationAnswerRepository.save(answer);
	}

	public List<ApplicationAnswer> getAllAnswers(Long userId, Long recruitId, Long applicationId) {
		return applicationAnswerRepository.findAllAnswers(userId, recruitId, applicationId);
	}
}
