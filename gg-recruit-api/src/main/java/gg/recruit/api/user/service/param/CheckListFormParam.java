package gg.recruit.api.user.service.param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.ApplicationAnswerCheckList;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.enums.InputType;

public class CheckListFormParam extends FormParam {
	private final List<Long> checkedList;

	public CheckListFormParam(List<Long> checkedList, Long questionId, InputType inputType) {
		this.checkedList = checkedList;
		this.questionId = questionId;
		this.inputType = inputType;
	}

	@Override
	public List<ApplicationAnswer> toApplicationAnswer(Application application, Map<Long, Question> questionMap,
		Map<Long, CheckList> checkListMap) {
		List<ApplicationAnswer> res = new ArrayList<>();
		for (Long checkListId : checkedList) {
			CheckList checkList = checkListMap.get(checkListId);
			if (checkList == null) {
				throw new IllegalArgumentException("checkListId must exist in checkListMap");
			}
			res.add(new ApplicationAnswerCheckList(application, questionMap.get(questionId), checkList));
		}
		return res;
	}
}
