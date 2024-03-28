package gg.recruit.api.user.service.param;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.ApplicationAnswerText;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TextFormParam extends FormParam {
	private String answer;

	public TextFormParam(String answer, Long questionId, InputType inputType) {
		this.answer = answer;
		this.questionId = questionId;
		this.inputType = inputType;
	}

	@Override
	public List<ApplicationAnswer> toApplicationAnswer(Application application, Map<Long, Question> questionMap,
		Map<Long, CheckList> checkListMap) {
		return Collections.singletonList(
			new ApplicationAnswerText(application, questionMap.get(questionId), answer));
	}
}
