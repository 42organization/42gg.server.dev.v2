package gg.party.api.user.template.service;

import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
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
	 * @return 템플릿 전체 리스트 (name 순으로 오름차순 정렬)
	 */
	@Transactional(readOnly = true)
	public TemplateListResDto findTemplateList() {
		return new TemplateListResDto(templateRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
			.map(TemplateResDto::new)
			.collect(Collectors.toList()));
	}
}
