package gg.recruit.api.user.application.service.response;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import gg.data.recruit.application.FormEntityDto;
import lombok.Getter;

@Getter
public class FormSvcDto {
	private Long questionId;
	private String inputType;

	private List<CheckListSvcDto> checkedList;
	private String answer;

	public FormSvcDto(Long questionId, List<FormEntityDto> entityDtos) {
		this.questionId = questionId;
		this.inputType = entityDtos.get(0).getInputType().name();
		this.answer = entityDtos.get(0).getAnswer();
		this.checkedList = entityDtos.stream().map(FormEntityDto::getCheckedList)
			.filter(Objects::nonNull)
			.map(CheckListSvcDto::new).collect(Collectors.toList());
	}
}
