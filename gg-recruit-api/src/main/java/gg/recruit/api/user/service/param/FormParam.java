package gg.recruit.api.user.service.param;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
public abstract class FormParam {
	protected Long questionId;

	protected InputType inputType;

	public abstract List<ApplicationAnswer> toApplicationAnswer(Application application,
		Map<Long, Question> questionMap, Map<Long, CheckList> checkListMap);
}
