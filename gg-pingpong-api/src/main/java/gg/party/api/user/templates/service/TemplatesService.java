package gg.party.api.user.templates.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.party.api.user.templates.controller.response.TemplatesResDto;
import gg.repo.party.TemplatesRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplatesService {
	private final TemplatesRepository templatesRepository;

	/**
	 * 템플릿 전체 조회
	 * @return 템플릿 전체 리스트 (id 순으로 오름차순 정렬)
	 */
	@Transactional(readOnly = true)
	public List<TemplatesResDto> findTemplatesList() {
		return templatesRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
			.map(TemplatesResDto::new)
			.collect(Collectors.toList());
	}
}
