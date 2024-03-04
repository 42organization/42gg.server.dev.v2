package gg.pingpong.api.user.user.controller;

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

import gg.data.game.type.Mode;
import gg.data.user.type.OauthType;
import gg.data.user.type.RoleType;
import gg.pingpong.api.global.dto.PageRequestDto;
import gg.pingpong.api.global.security.cookie.CookieUtil;
import gg.pingpong.api.global.security.jwt.utils.TokenHeaders;
import gg.auth.argumentresolver.Login;
import gg.pingpong.api.user.user.controller.request.UserModifyRequestDto;
import gg.pingpong.api.user.user.controller.request.UserProfileImageRequestDto;
import gg.pingpong.api.user.user.controller.response.UserAttendanceResponseDto;
import gg.pingpong.api.user.user.controller.response.UserCoinHistoryListResponseDto;
import gg.pingpong.api.user.user.controller.response.UserCoinResponseDto;
import gg.pingpong.api.user.user.controller.response.UserDetailResponseDto;
import gg.pingpong.api.user.user.controller.response.UserHistoryResponseDto;
import gg.pingpong.api.user.user.controller.response.UserImageResponseDto;
import gg.pingpong.api.user.user.controller.response.UserLiveResponseDto;
import gg.pingpong.api.user.user.controller.response.UserNormalDetailResponseDto;
import gg.pingpong.api.user.user.controller.response.UserRankResponseDto;
import gg.pingpong.api.user.user.controller.response.UserSearchResponseDto;
import gg.pingpong.api.user.user.dto.UserAccessTokenDto;
import gg.pingpong.api.user.user.dto.UserBackgroundDto;
import gg.auth.UserDto;
import gg.pingpong.api.user.user.dto.UserEdgeDto;
import gg.pingpong.api.user.user.dto.UserOauthDto;
import gg.pingpong.api.user.user.dto.UserTextColorDto;
import gg.pingpong.api.user.user.service.UserAuthenticationService;
import gg.pingpong.api.user.user.service.UserCoinService;
import gg.pingpong.api.user.user.service.UserService;
import gg.utils.exception.user.KakaoOauth2AlreadyExistException;
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
