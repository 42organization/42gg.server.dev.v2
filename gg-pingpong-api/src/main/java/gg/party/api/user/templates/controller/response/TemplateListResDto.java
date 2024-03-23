package gg.party.api.user.templates.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TemplateListResDto {
	private List<TemplateResDto> templateList;

	public TemplateListResDto(List<TemplateResDto> templateListResDto) {
		this.templateList = templateListResDto;
	}
}
