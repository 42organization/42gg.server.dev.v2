package gg.party.api.user.templates.service;

import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.party.api.user.templates.controller.response.TemplateListResDto;
import gg.party.api.user.templates.controller.response.TemplateResDto;
import gg.repo.party.TemplateRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {
	private final TemplateRepository templateRepository;

	/**
	 * 템플릿 전체 조회
	 * @return 템플릿 전체 리스트 (id 순으로 오름차순 정렬)
	 */
	@Transactional(readOnly = true)
	public TemplateListResDto findTemplateList() {
		return new TemplateListResDto(templateRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
			.map(TemplateResDto::new)
			.collect(Collectors.toList()));
	}
}
