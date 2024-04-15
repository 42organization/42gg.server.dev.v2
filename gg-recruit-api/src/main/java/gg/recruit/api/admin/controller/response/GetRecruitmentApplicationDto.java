package gg.recruit.api.admin.controller.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.ApplicationAnswerCheckList;
import gg.data.recruit.application.ApplicationAnswerText;
import gg.data.recruit.application.enums.ApplicationStatus;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GetRecruitmentApplicationDto {
	private Long applicationId;
	private String intraId;
	private ApplicationStatus status;
	private List<Form> forms = new ArrayList<>();

	public GetRecruitmentApplicationDto(Long applicationId, String intraId, ApplicationStatus status) {
		this.applicationId = applicationId;
		this.intraId = intraId;
		this.status = status;
	}

	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@Getter
	public static class Form {
		private Long questionId;
		private String question;
		private InputType inputType;
		private String answer;
		private List<CheckListForm> checkedList = new ArrayList<>();

		public Form(Long questionId, String question, InputType inputType) {
			this.questionId = questionId;
			this.question = question;
			this.inputType = inputType;
		}

		@Mapper
		public interface MapStruct {
			MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

			@Mapping(source = "id", target = "questionId")
			Form entityToDto(Question question);
		}

		@NoArgsConstructor(access = AccessLevel.PROTECTED)
		@Getter
		public static class CheckListForm {
			private Long checkId;
			private String contents;

			public CheckListForm(Long checkId, String content) {
				this.checkId = checkId;
				this.contents = content;
			}

			@Mapper
			public interface MapStruct {
				MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

				@Mapping(source = "id", target = "checkId")
				CheckListForm entityToDto(CheckList checkList);
			}
		}
	}

	@Mapper
	public interface MapStruct {
		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		@Mapping(source = "id", target = "applicationId")
		@Mapping(source = "user.intraId", target = "intraId")
		GetRecruitmentApplicationDto entityToDto(Application application);

		@AfterMapping
		default void fillForms(Application application, @MappingTarget GetRecruitmentApplicationDto dto) {
			Set<Long> questionIdSet = new HashSet<>();
			HashSet<Form> formSet = new LinkedHashSet<>();
			HashMap<Long, Form> formMap = new HashMap<>();
			for (ApplicationAnswer answer : application.getApplicationAnswers()) {
				Long questionId = answer.getQuestionId();
				if (!questionIdSet.contains(questionId)) {
					questionIdSet.add(questionId);
					Form form = Form.MapStruct.INSTANCE.entityToDto(answer.getQuestion());
					formSet.add(form);
					formMap.put(questionId, form);
				}
				Form form = formMap.get(questionId);
				if (answer instanceof ApplicationAnswerCheckList) {
					ApplicationAnswerCheckList applicationAnswerCheckList = (ApplicationAnswerCheckList)answer;
					CheckList checkList = applicationAnswerCheckList.getCheckList();
					form.checkedList.add(Form.CheckListForm.MapStruct.INSTANCE.entityToDto(checkList));
				} else if (answer instanceof ApplicationAnswerText) {
					ApplicationAnswerText applicationAnswerText = (ApplicationAnswerText)answer;
					form.answer = applicationAnswerText.getAnswer();
				}
			}
			dto.forms = formSet.stream().collect(Collectors.toList());
		}
	}
}
