package gg.party.api.user.template.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.party.api.user.template.controller.response.TemplateListResDto;
import gg.party.api.user.template.controller.response.TemplateResDto;
import gg.repo.party.TemplateRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {
	private final TemplateRepository templateRepository;

	/**
	 * 템플릿 전체 조회
	 * @return 템플릿 전체 리스트 (가나다 순으로 정렬)
	 */
	@Transactional(readOnly = true)
	public TemplateListResDto findTemplateList() {
		List<TemplateResDto> sortedTemplates = templateRepository.findAll()
			.stream()
			.map(TemplateResDto::new)
			.sorted(Comparator.comparing(TemplateResDto::getGameName, Comparator.nullsLast(Comparator.naturalOrder())))
			.collect(Collectors.toList());

		return new TemplateListResDto(sortedTemplates);
	}
}
