package gg.pingpong.api.admin.user.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gg.pingpong.admin.repo.season.SeasonAdminRepository;
import gg.pingpong.admin.repo.user.UserAdminRepository;
import gg.pingpong.admin.repo.user.UserImageAdminRepository;
import gg.pingpong.api.admin.rank.service.RankRedisAdminService;
import gg.pingpong.api.admin.user.controller.request.UserUpdateAdminRequestDto;
import gg.pingpong.api.admin.user.controller.response.UserDetailAdminResponseDto;
import gg.pingpong.api.admin.user.controller.response.UserImageListAdminResponseDto;
import gg.pingpong.api.admin.user.controller.response.UserSearchAdminResponseDto;
import gg.pingpong.api.admin.user.dto.UserImageAdminDto;
import gg.pingpong.api.admin.user.dto.UserSearchAdminDto;
import gg.pingpong.api.global.utils.aws.AsyncNewUserImageUploader;
import gg.pingpong.api.user.user.service.UserFindService;
import gg.pingpong.data.rank.Rank;
import gg.pingpong.data.season.Season;
import gg.pingpong.data.rank.redis.RankRedis;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.UserImage;
import gg.pingpong.data.user.type.RoleType;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.utils.RedisKeyManager;
import gg.pingpong.utils.exception.rank.RankNotFoundException;
import gg.pingpong.utils.exception.rank.RedisDataNotFoundException;
import gg.pingpong.utils.exception.season.SeasonNotFoundException;
import gg.pingpong.utils.exception.user.UserNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserAdminService {

	private final UserAdminRepository userAdminRepository;
	private final SeasonAdminRepository seasonAdminRepository;
	private final RankRepository rankRepository;
	private final RankRedisRepository rankRedisRepository;
	private final RankRedisAdminService rankRedisAdminService;
	private final AsyncNewUserImageUploader asyncNewUserImageUploader;
	private final UserFindService userFindService;
	private final UserImageAdminRepository userImageAdminRepository;

	@Transactional(readOnly = true)
	public UserSearchAdminResponseDto searchAll(Pageable pageable) {
		Page<User> userPage = userAdminRepository.findAll(pageable);
		List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
		for (User user : userPage.getContent()) {
			userSearchAdminDtos.add(new UserSearchAdminDto(user, userFindService.getUserStatusMessage(user)));
		}
		return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
	}

	@Transactional(readOnly = true)
	public UserSearchAdminResponseDto searchByIntraId(Pageable pageable, String intraId) {
		Page<User> userPage = userAdminRepository.findByIntraId(pageable, intraId);
		List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
		for (User user : userPage.getContent()) {
			userSearchAdminDtos.add(new UserSearchAdminDto(user, userFindService.getUserStatusMessage(user)));
		}
		return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
	}

	/* 문자열을 포함하는 intraId를 가진 유저 찾기 */
	@Transactional(readOnly = true)
	public UserSearchAdminResponseDto findByPartsOfIntraId(String intraId, Pageable pageable) {
		Page<User> userPage = userAdminRepository.findByIntraIdContains(pageable, intraId);
		List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
		for (User user : userPage.getContent()) {
			userSearchAdminDtos.add(new UserSearchAdminDto(user, userFindService.getUserStatusMessage(user)));
		}
		return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
	}

	@Transactional(readOnly = true)
	public UserDetailAdminResponseDto getUserDetailByIntraId(String intraId) {
		User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
		Season currSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now())
			.orElseThrow(SeasonNotFoundException::new);
		try {
			RankRedis userCurrRank = rankRedisRepository.findRankByUserId(
				RedisKeyManager.getHashKey(currSeason.getId()),
				user.getId());
			return new UserDetailAdminResponseDto(user, userCurrRank);
		} catch (RedisDataNotFoundException e) {
			return new UserDetailAdminResponseDto(user);
		}
	}

	@Transactional
	public void updateUserDetail(String intraId,
		UserUpdateAdminRequestDto userUpdateAdminRequestDto,
		MultipartFile userImageFile) throws IOException {
		Season currSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now())
			.orElseThrow(() -> new SeasonNotFoundException());
		User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);

		user.modifyUserDetail(userUpdateAdminRequestDto.getEmail(), userUpdateAdminRequestDto.getRacketType(),
			RoleType.of(userUpdateAdminRequestDto.getRoleType()), userUpdateAdminRequestDto.getCoin());
		if (userImageFile != null) {
			asyncNewUserImageUploader.update(intraId, userImageFile);
		}
		updateUserRank(user.getId(), currSeason.getId(), userUpdateAdminRequestDto);
	}

	private void updateUserRank(Long userId, Long currSeasonId, UserUpdateAdminRequestDto updateReq) {
		Rank userCurrRank = rankRepository.findByUserIdAndSeasonId(userId, currSeasonId)
			.orElseThrow(RankNotFoundException::new);
		RankRedis userCurrRankRedis = rankRedisRepository.findRankByUserId(RedisKeyManager.getHashKey(currSeasonId),
			userId);

		userCurrRank.modifyUserRank(updateReq.getPpp(),
			updateReq.getWins(),
			updateReq.getLosses());
		userCurrRank.setStatusMessage(updateReq.getStatusMessage());

		userCurrRankRedis.changedRank(updateReq.getPpp(),
			updateReq.getWins(),
			updateReq.getLosses());
		userCurrRankRedis.setStatusMessage(updateReq.getStatusMessage());
		rankRedisAdminService.updateRankUser(RedisKeyManager.getHashKey(currSeasonId),
			RedisKeyManager.getZSetKey(currSeasonId),
			userId, userCurrRankRedis);
	}

	public String getUserImageToString(User user) {
		UserImage userImage = userImageAdminRepository.findTopByUserAndDeletedAtIsNullOrderByCreatedAtDesc(user)
			.orElse(null);
		if (userImage == null) {
			return "null";
		} else {
			userImage.updateIsCurrent();
			return userImage.getImageUri();
		}
	}

	@Transactional
	public void deleteUserProfileImage(String intraId) {
		User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
		UserImage userImage = userImageAdminRepository.findTopByUserAndIsCurrentIsTrueOrderByCreatedAtDesc(user)
			.orElseThrow(UserNotFoundException::new);
		userImage.updateDeletedAt(LocalDateTime.now());
		String userImageUri = getUserImageToString(user);
		user.updateImageUri(userImageUri);
	}

	@Transactional(readOnly = true)
	public UserImageListAdminResponseDto getUserImageDeleteList(Pageable pageable) {
		Page<UserImage> userImagePage = userImageAdminRepository.findAllByDeletedAtNotNullOrderByDeletedAtDesc(
			pageable);
		Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

		return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
	}

	@Transactional(readOnly = true)
	public UserImageListAdminResponseDto getUserImageDeleteListByIntraId(Pageable pageable, String intraId) {
		User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
		Page<UserImage> userImagePage = userImageAdminRepository.findAllByUserAndDeletedAtNotNullOrderByDeletedAtDesc(
			user.getId(), pageable);
		Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

		return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
	}

	@Transactional(readOnly = true)
	public UserImageListAdminResponseDto getUserImageList(Pageable pageable) {
		Page<UserImage> userImagePage = userImageAdminRepository.findAllChangedOrderByCreatedAtDesc(pageable);
		Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

		return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
	}

	@Transactional(readOnly = true)
	public UserImageListAdminResponseDto getUserImageListByIntraId(Pageable pageable, String intraId) {
		User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
		Page<UserImage> userImagePage = userImageAdminRepository.findAllByUserOrderByCreatedAtDesc(user.getId(),
			pageable);
		Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

		return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
	}

	@Transactional(readOnly = true)
	public UserImageListAdminResponseDto getUserImageCurrent(Pageable pageable) {
		Page<UserImage> userImagePage = userImageAdminRepository.findAllByIsCurrentTrueOrderByCreatedAtDesc(pageable);
		Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

		return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
	}

	@Transactional(readOnly = true)
	public UserImageListAdminResponseDto getUserImageCurrentByIntraId(Pageable pageable, String intraId) {
		User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
		Page<UserImage> userImagePage = userImageAdminRepository.findAllByUserAndIsCurrentTrueOrderByCreatedAtDesc(
			user.getId(), pageable);
		Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

		return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
	}
}
