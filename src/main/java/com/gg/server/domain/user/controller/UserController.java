package com.gg.server.domain.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gg.server.data.game.type.Mode;
import com.gg.server.data.user.type.OauthType;
import com.gg.server.data.user.type.RoleType;
import com.gg.server.domain.user.dto.UserAccessTokenDto;
import com.gg.server.domain.user.dto.UserAttendanceResponseDto;
import com.gg.server.domain.user.dto.UserBackgroundDto;
import com.gg.server.domain.user.dto.UserCoinHistoryListResponseDto;
import com.gg.server.domain.user.dto.UserCoinResponseDto;
import com.gg.server.domain.user.dto.UserDetailResponseDto;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.dto.UserEdgeDto;
import com.gg.server.domain.user.dto.UserHistoryResponseDto;
import com.gg.server.domain.user.dto.UserImageResponseDto;
import com.gg.server.domain.user.dto.UserLiveResponseDto;
import com.gg.server.domain.user.dto.UserModifyRequestDto;
import com.gg.server.domain.user.dto.UserNormalDetailResponseDto;
import com.gg.server.domain.user.dto.UserOauthDto;
import com.gg.server.domain.user.dto.UserProfileImageRequestDto;
import com.gg.server.domain.user.dto.UserRankResponseDto;
import com.gg.server.domain.user.dto.UserSearchResponseDto;
import com.gg.server.domain.user.dto.UserTextColorDto;
import com.gg.server.domain.user.exception.KakaoOauth2AlreadyExistException;
import com.gg.server.domain.user.service.UserAuthenticationService;
import com.gg.server.domain.user.service.UserCoinService;
import com.gg.server.domain.user.service.UserService;
import com.gg.server.global.dto.PageRequestDto;
import com.gg.server.global.security.cookie.CookieUtil;
import com.gg.server.global.security.jwt.utils.TokenHeaders;
import com.gg.server.global.utils.argumentresolver.Login;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/users")
public class UserController {
	private final UserService userService;
	private final UserAuthenticationService userAuthenticationService;
	private final CookieUtil cookieUtil;
	private final UserCoinService userCoinService;

	@PostMapping("/accesstoken")
	public ResponseEntity<UserAccessTokenDto> generateAccessToken(@RequestParam String refreshToken) {
		String accessToken = userAuthenticationService.regenerate(refreshToken);
		return new ResponseEntity<>(new UserAccessTokenDto(accessToken), HttpStatus.CREATED);
	}

	@GetMapping
	UserNormalDetailResponseDto getUserNormalDetail(@Parameter(hidden = true) @Login UserDto user) {
		return userService.getUserNormalDetail(user);
	}

	@GetMapping("/live")
	UserLiveResponseDto getUserLiveDetail(@Parameter(hidden = true) @Login UserDto user) {
		return userService.getUserLiveDetail(user);
	}

	@GetMapping("/searches")
	UserSearchResponseDto searchUsers(@RequestParam String intraId) {
		List<String> intraIds = userService.findByPartOfIntraId(intraId);
		return new UserSearchResponseDto(intraIds);
	}

	@GetMapping("/{intraId}")
	public UserDetailResponseDto getUserDetail(@PathVariable String intraId) {
		return userService.getUserDetail(intraId);
	}

	@GetMapping("/{intraId}/rank")
	public UserRankResponseDto getUserRank(@PathVariable String intraId, @RequestParam Long season) {
		return userService.getUserRankDetail(intraId, season);
	}

	@GetMapping("/{intraId}/historics")
	public UserHistoryResponseDto getUserHistory(@PathVariable String intraId, @RequestParam Long season) {
		return userService.getUserHistory(intraId, season);
	}

	@PutMapping("{intraId}")
	public ResponseEntity doModifyUser(@Valid @RequestBody UserModifyRequestDto userModifyRequestDto,
		@PathVariable String intraId, @Parameter(hidden = true) @Login UserDto loginUser) {
		if (!loginUser.getIntraId().equals(intraId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		userService.updateUser(userModifyRequestDto.getRacketType(), userModifyRequestDto.getStatusMessage(),
			userModifyRequestDto.getSnsNotiOpt(), intraId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/logout")
	public void logout(HttpServletResponse response) {
		cookieUtil.deleteCookie(response, TokenHeaders.REFRESH_TOKEN);
	}

	/**
	 * 42user 카카오 로그인 연동 해제
	 */
	@DeleteMapping("/oauth/kakao")
	public void deleteOauthKakao(@Parameter(hidden = true) @Login UserDto user) {
		if (user.getRoleType().equals(RoleType.GUEST) || user.getKakaoId() == null) {
			throw new KakaoOauth2AlreadyExistException();
		}
		userService.deleteKakaoId(user.getId());
	}

	@GetMapping("/oauth")
	public UserOauthDto getUserOauth2Information(@Parameter(hidden = true) @Login UserDto user) {
		return new UserOauthDto(OauthType.of(user.getRoleType(), user.getKakaoId()).getCode());
	}

	@GetMapping("/top3")
	public UserImageResponseDto getUserImage(@RequestParam(required = false) Long seasonId, Mode mode) {
		if (mode == Mode.RANK) {
			return userService.getRankedUserImagesByPPP(seasonId);
		} else {
			PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "totalExp"));
			return userService.getRankedUserImagesByExp(pageRequest);
		}
	}

	@PatchMapping("/text-color")
	public ResponseEntity updateTextColor(@RequestBody @Valid UserTextColorDto textColorDto,
		@Parameter(hidden = true) @Login UserDto user) {
		userService.updateTextColor(user.getId(), textColorDto);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/attendance")
	public UserAttendanceResponseDto attendUser(@Parameter(hidden = true) @Login UserDto user) {
		return userService.attendUser(user.getId());
	}

	@PatchMapping("/edge")
	public String updateEdge(@RequestBody @Valid UserEdgeDto userEdgeDto,
		@Parameter(hidden = true) @Login UserDto user) {
		String edge = userService.updateEdge(user, userEdgeDto);
		return "{\"edge\": " + "\"" + edge + "\"" + "}";
	}

	@GetMapping("/coin")
	public UserCoinResponseDto getUserCoin(@Parameter(hidden = true) @Login UserDto user) {
		return userCoinService.getUserCoin(user.getIntraId());
	}

	@PatchMapping("/background")
	public String updateBackground(@RequestBody @Valid UserBackgroundDto userBackgroundDto,
		@Parameter(hidden = true) @Login UserDto user) {
		String background = userService.updateBackground(user, userBackgroundDto);
		return "{\"background\": " + "\"" + background + "\"" + "}";
	}

	@GetMapping("/coinhistory")
	public UserCoinHistoryListResponseDto getUserCoinHistory(@ModelAttribute @Valid PageRequestDto coReq,
		@Parameter(hidden = true) @Login UserDto user) {
		Pageable pageable = PageRequest.of(coReq.getPage() - 1, coReq.getSize(), Sort.by("createdAt").descending());

		return userCoinService.getUserCoinHistory(pageable, user.getIntraId());
	}

	@PostMapping(path = "/profile-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
		MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity updateUserProfileImage(@RequestPart(required = false) MultipartFile profileImage,
		@RequestPart @Valid UserProfileImageRequestDto userProfileImageRequestDto,
		@Parameter(hidden = true) @Login UserDto user) throws IOException {
		userService.updateUserProfileImage(user, userProfileImageRequestDto, profileImage);
		return ResponseEntity.noContent().build();
	}
}
