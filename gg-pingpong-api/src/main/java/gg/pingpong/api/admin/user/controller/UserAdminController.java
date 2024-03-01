package gg.pingpong.api.admin.user.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gg.pingpong.api.admin.user.controller.request.UserSearchAdminRequestDto;
import gg.pingpong.api.admin.user.controller.request.UserUpdateAdminRequestDto;
import gg.pingpong.api.admin.user.controller.response.UserDetailAdminResponseDto;
import gg.pingpong.api.admin.user.controller.response.UserImageListAdminResponseDto;
import gg.pingpong.api.admin.user.controller.response.UserSearchAdminResponseDto;
import gg.pingpong.api.admin.user.service.UserAdminService;
import gg.pingpong.api.global.dto.PageRequestDto;
import gg.utils.exception.user.UserImageLargeException;
import gg.utils.exception.user.UserImageTypeException;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/users")
public class UserAdminController {

	private final UserAdminService userAdminService;

	@GetMapping
	public UserSearchAdminResponseDto userSearchAll(@ModelAttribute @Valid UserSearchAdminRequestDto searchRequestDto) {
		Pageable pageable = PageRequest.of(searchRequestDto.getPage() - 1,
			searchRequestDto.getSize(),
			Sort.by("intraId").ascending());
		if (searchRequestDto.getUserFilter() != null) {
			return userAdminService.searchByIntraId(pageable, searchRequestDto.getUserFilter());
		} else if (searchRequestDto.getIntraId() != null) {
			return userAdminService.findByPartsOfIntraId(searchRequestDto.getIntraId(), pageable);
		} else {
			return userAdminService.searchAll(pageable);
		}
	}

	@GetMapping("/{intraId}")
	public UserDetailAdminResponseDto userGetDetail(@PathVariable String intraId) {
		return userAdminService.getUserDetailByIntraId(intraId);
	}

	@PutMapping("/{intraId}")
	public ResponseEntity userUpdateDetail(@PathVariable String intraId,
		@RequestPart UserUpdateAdminRequestDto updateUserInfo,
		@RequestPart(required = false) MultipartFile imgData) throws IOException {
		if (imgData != null) {
			if (imgData.getSize() > 50000) {
				throw new UserImageLargeException();
			} else if (imgData.getContentType() == null || !imgData.getContentType().equals("image/jpeg")) {
				throw new UserImageTypeException();
			}
		}
		userAdminService.updateUserDetail(intraId, updateUserInfo, imgData);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/images/{intraId}")
	public ResponseEntity deleteUserProfileImage(@PathVariable String intraId) {
		userAdminService.deleteUserProfileImage(intraId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/delete-list")
	public UserImageListAdminResponseDto getUserImageDeleteList(@ModelAttribute @Valid PageRequestDto pageRequestDto) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1,
			pageRequestDto.getSize(),
			Sort.by("id").descending());
		return userAdminService.getUserImageDeleteList(pageable);
	}

	@GetMapping("/delete-list/{intraId}")
	public UserImageListAdminResponseDto getUserImageDeleteListByIntraId(
		@ModelAttribute @Valid PageRequestDto pageRequestDto, @PathVariable String intraId) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1,
			pageRequestDto.getSize());
		return userAdminService.getUserImageDeleteListByIntraId(pageable, intraId);
	}

	@GetMapping("/images")
	public UserImageListAdminResponseDto getUserImageList(@ModelAttribute @Valid PageRequestDto pageRequestDto) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1,
			pageRequestDto.getSize());
		return userAdminService.getUserImageList(pageable);
	}

	@GetMapping("/images/{intraId}")
	public UserImageListAdminResponseDto getUserImage(@ModelAttribute @Valid PageRequestDto pageRequestDto,
		@PathVariable String intraId) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1,
			pageRequestDto.getSize());
		return userAdminService.getUserImageListByIntraId(pageable, intraId);
	}

	@GetMapping("/images/current")
	public UserImageListAdminResponseDto getUserImageCurrent(@ModelAttribute @Valid PageRequestDto pageRequestDto) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1,
			pageRequestDto.getSize());
		return userAdminService.getUserImageCurrent(pageable);
	}

	@GetMapping("/images/current/{intraId}")
	public UserImageListAdminResponseDto getUserImageCurrentByIntraId(
		@ModelAttribute @Valid PageRequestDto pageRequestDto, @PathVariable String intraId) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1,
			pageRequestDto.getSize());
		return userAdminService.getUserImageCurrentByIntraId(pageable, intraId);
	}
}
