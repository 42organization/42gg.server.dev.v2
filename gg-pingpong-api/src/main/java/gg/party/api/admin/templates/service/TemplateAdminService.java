package gg.party.api.admin.templates.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.repo.party.TemplateRepository;
import gg.utils.exception.party.TemplateNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateAdminService {
	private final TemplateRepository templateRepository;

	/**
	 * 템플릿 삭제
	 */
	@Transactional
	public void removeTemplate(Long templateId) {
		if (!templateRepository.existsById(templateId)) {
			throw new TemplateNotFoundException();
		}
		templateRepository.deleteById(templateId);
	}
}
