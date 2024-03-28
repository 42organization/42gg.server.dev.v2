package gg.recruit.api.user.controller.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.user.service.param.FormParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "inputType", visible = true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = TextFormRequest.class, name = "TEXT"),
	@JsonSubTypes.Type(value = CheckListFormRequest.class, name = "SINGLE_CHECK"),
	@JsonSubTypes.Type(value = CheckListFormRequest.class, name = "MULTI_CHECK")
})
@Setter
@Getter
@NoArgsConstructor
public abstract class FormRequest {
	protected Long questionId;
	protected InputType inputType;

	public abstract FormParam toFormParam();
}
