package gg.party.api.user.template.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TemplateListResDto {
	private List<TemplateResDto> templateList;

	public TemplateListResDto(List<TemplateResDto> templateListResDto) {
		this.templateList = templateListResDto;
	}
}
