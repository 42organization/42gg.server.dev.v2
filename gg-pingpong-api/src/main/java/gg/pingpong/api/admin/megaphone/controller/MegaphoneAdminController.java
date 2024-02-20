package gg.pingpong.api.admin.megaphone.controller;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.admin.megaphone.dto.MegaphoneHistoryResponseDto;
import com.gg.server.admin.megaphone.service.MegaphoneAdminService;
import com.gg.server.global.dto.PageRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/megaphones")
public class MegaphoneAdminController {
	private final MegaphoneAdminService megaphoneAdminService;

	@GetMapping("/history")
	public MegaphoneHistoryResponseDto getMegaphoneHistory(@ModelAttribute @Valid PageRequestDto pageRequestDto,
		@RequestParam(required = false) String intraId) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize(),
			Sort.by("id").descending());
		if (intraId == null) {
			return megaphoneAdminService.getMegaphoneHistory(pageable);
		}
		return megaphoneAdminService.getMegaphoneHistoryByIntraId(intraId, pageable);

	}
}
