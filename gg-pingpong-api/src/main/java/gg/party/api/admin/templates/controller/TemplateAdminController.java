package gg.party.api.admin.templates.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.templates.service.TemplateAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/templates")
public class TemplateAdminController {
	private final TemplateAdminService templateAdminService;

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
