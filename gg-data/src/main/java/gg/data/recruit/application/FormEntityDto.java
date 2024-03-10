package gg.data.recruit.application;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.Getter;

@Getter
public class FormEntityDto {
	private Long questionId;
	private InputType inputType;

	private CheckListEntityDto checkedList;
	private String answer;

	public FormEntityDto(Long questionId, InputType inputType, CheckListEntityDto checkedList) {
		this.questionId = questionId;
		this.inputType = inputType;
		this.checkedList = checkedList;
	}

	public FormEntityDto(Long questionId, InputType inputType, String answer) {
		this.questionId = questionId;
		this.inputType = inputType;
		this.answer = answer;
	}
}
