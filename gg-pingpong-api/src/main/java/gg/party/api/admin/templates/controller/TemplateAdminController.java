package gg.party.api.admin.templates.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.templates.controller.request.TemplateAdminCreateReqDto;
import gg.party.api.admin.templates.controller.request.TemplateAdminUpdateReqDto;
import gg.party.api.admin.templates.service.TemplateAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/templates")
public class TemplateAdminController {
	private final TemplateAdminService templateAdminService;

	/**
	 * 템플릿 추가
	 * return 201 status code(성공적인 추가 status)
	 */
	@PostMapping
	public ResponseEntity<Void> addTemplate(@RequestBody TemplateAdminCreateReqDto request) {
		templateAdminService.addTemplate(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 템플릿 수정
	 * return 204 status code(성공적인 수정 status)
	 */
	@PatchMapping("/{templateId}")
	public ResponseEntity<Void> updateTemplate(@PathVariable Long templateId,
		@RequestBody TemplateAdminUpdateReqDto request) {
		templateAdminService.modifyTemplate(templateId, request);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 템플릿 삭제
	 * return 204 status code(성공적인 삭제 status)
	 */
	@DeleteMapping("/{templateId}")
	public ResponseEntity<Void> removeTemplate(@PathVariable Long templateId) {
		templateAdminService.removeTemplate(templateId);
		return ResponseEntity.noContent().build();
	}
}
