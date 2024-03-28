package gg.recruit.api.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import gg.data.recruit.recruitment.Question;
import gg.repo.recruit.user.recruitment.QuestionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	private final QuestionRepository questionRepository;

	public List<Question> findQuestionsByRecruitId(Long recruitId) {
		return questionRepository.findAllByRecruitId(recruitId);
	}
}
