package gg.party.api.admin.templates.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.Category;
import gg.data.party.GameTemplate;
import gg.party.api.admin.templates.controller.request.TemplateAdminCreateReqDto;
import gg.party.api.admin.templates.controller.request.TemplateAdminUpdateReqDto;
import gg.repo.party.CategoryRepository;
import gg.repo.party.TemplateRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.party.CategoryNotFoundException;
import gg.utils.exception.party.RoomMinMaxPeople;
import gg.utils.exception.party.TemplateNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateAdminService {
	private final TemplateRepository templateRepository;
	private final CategoryRepository categoryRepository;

	/**
	 * 템플릿 추가
	 * @exception CategoryNotFoundException 존재하지 않는 카테고리 입력 - 404
	 */
	public void addTemplate(TemplateAdminCreateReqDto request) {
		Category category = categoryRepository.findByName(request.getCategoryName());
		if (category == null) {
			throw new CategoryNotFoundException();
		}
		GameTemplate gameTemplate = TemplateAdminCreateReqDto.toEntity(request, category);
		templateRepository.save(gameTemplate);
	}

	/**
	 * 템플릿 수정
	 * @throws TemplateNotFoundException 존재하지 않는 템플릿 입력 - 404
	 * @throws RoomMinMaxPeople 최소인원이 최대인원보다 큰 경우 - 400
	 * @throws CategoryNotFoundException 존재하지 않는 카테고리 입력 - 404
	 */
	@Transactional
	public void modifyTemplate(Long templateId, TemplateAdminUpdateReqDto request) {
		GameTemplate template = templateRepository.findById(templateId)
			.orElseThrow(TemplateNotFoundException::new);

		if (request.getMaxGamePeople() < request.getMinGamePeople()) {
			throw new RoomMinMaxPeople(ErrorCode.ROOM_MIN_MAX_PEOPLE);
		}
		request.updateEntity(template);
		if (request.getCategoryName() != null) {
			Category newCategory = categoryRepository.findByName(request.getCategoryName());
			if (newCategory == null) {
				throw new CategoryNotFoundException();
			}
			template.modifyCategory(newCategory);
		}

		templateRepository.save(template);
	}

	/**
	 * 템플릿 삭제
	 * @exception TemplateNotFoundException 존재하지 않는 템플릿 입력 - 404
	 */
	@Transactional
	public void removeTemplate(Long templateId) {
		if (!templateRepository.existsById(templateId)) {
			throw new TemplateNotFoundException();
		}
		templateRepository.deleteById(templateId);
	}
}
