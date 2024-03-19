package gg.recruit.api.user;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import gg.data.recruit.application.Application;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitments;
import gg.data.recruit.recruitment.enums.InputType;
import gg.data.user.User;
import gg.repo.recruit.user.application.ApplicationRepository;
import gg.repo.recruit.user.recruitment.QuestionRepository;
import gg.repo.recruit.user.recruitment.RecruitmentRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecruitMockData {
	private final RecruitmentRepository recruitmentRepository;
	private final ApplicationRepository applicationRepository;
	private final QuestionRepository questionRepository;

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

	public Application createApplication(User user, Recruitments recruitments) {
		Application application = new Application(user, recruitments);
		return applicationRepository.save(application);
	}

	public Question createQuestion(Recruitments recruitments) {
		Question question = new Question(recruitments, InputType.TEXT, "question", 1);
		return questionRepository.save(question);
	}
}
