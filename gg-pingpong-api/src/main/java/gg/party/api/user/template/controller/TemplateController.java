package gg.party.api.user.template.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.user.template.controller.response.TemplateListResDto;
import gg.party.api.user.template.service.TemplateService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/templates")
public class TemplateController {
	private final TemplateService templateService;

	/**
	 * 템플릿 전체 조회
	 * @return 템플릿 전체 리스트 (가나다 순으로 정렬) 200
	 */
	@GetMapping
	public ResponseEntity<TemplateListResDto> templateList() {
		return ResponseEntity.status(HttpStatus.OK).body(templateService.findTemplateList());
	}
}
