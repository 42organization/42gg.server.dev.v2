package gg.pingpong.api.admin.noti.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.admin.noti.controller.request.NotiListAdminRequestDto;
import gg.pingpong.api.admin.noti.controller.request.SendNotiAdminRequestDto;
import gg.pingpong.api.admin.noti.controller.response.NotiListAdminResponseDto;
import gg.pingpong.api.admin.noti.service.NotiAdminService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/notifications")
public class NotiAdminController {

	private final NotiAdminService notiAdminService;

	@GetMapping
	public NotiListAdminResponseDto getAllNoti(@ModelAttribute NotiListAdminRequestDto requestDto) {
		int page = requestDto.getPage();
		int size = requestDto.getSize();
		String intraId = requestDto.getIntraId();

		Pageable pageable = PageRequest.of(page - 1, size,
			Sort.by("createdAt").descending().and(Sort.by("user.intraId").ascending()));
		if (intraId == null) {
			return notiAdminService.getAllNoti(pageable);
		} else {
			return notiAdminService.getFilteredNotifications(pageable, intraId);
		}
	}

	@PostMapping
	public ResponseEntity sendNotiToUser(@RequestBody SendNotiAdminRequestDto sendNotiAdminRequestDto) {
		notiAdminService.sendAnnounceNotiToUser(sendNotiAdminRequestDto);
		return new ResponseEntity(HttpStatus.CREATED);
	}
}
