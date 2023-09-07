package com.gg.server.admin.user.service;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.admin.user.data.UserImageAdminRepository;
import com.gg.server.admin.user.dto.*;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RankNotFoundException;
import com.gg.server.domain.rank.exception.RedisDataNotFoundException;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserImage;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.service.UserFindService;
import com.gg.server.global.utils.aws.AsyncNewUserImageUploader;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, userFindService.getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UserSearchAdminResponseDto searchByIntraId(Pageable pageable, String intraId) {
        Page<User> userPage = userAdminRepository.findByIntraId(pageable, intraId);
        List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, userFindService.getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
    }

    /* 문자열을 포함하는 intraId를 가진 유저 찾기 */
    @Transactional(readOnly = true)
    public UserSearchAdminResponseDto findByPartsOfIntraId(String intraId, Pageable pageable) {
        Page<User> userPage = userAdminRepository.findByIntraIdContains(pageable, intraId);
        List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, userFindService.getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UserDetailAdminResponseDto getUserDetailByIntraId(String intraId) {
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(() -> new UserNotFoundException());
        Season currSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now()).orElseThrow(() -> new SeasonNotFoundException());
        try {
            RankRedis userCurrRank = rankRedisRepository.findRankByUserId(RedisKeyManager.getHashKey(currSeason.getId()),
                    user.getId());
           return new UserDetailAdminResponseDto(user, userCurrRank);
        } catch (RedisDataNotFoundException e){
            return new UserDetailAdminResponseDto(user);
        }
    }

    @Transactional
    public void updateUserDetail(String intraId,
                                 UserUpdateAdminRequestDto userUpdateAdminRequestDto,
                                 MultipartFile userImageFile) throws IOException{
        Season currSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now()).orElseThrow(() -> new SeasonNotFoundException());
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(() -> new UserNotFoundException());

        user.modifyUserDetail(userUpdateAdminRequestDto);
        if (userImageFile != null)
            asyncNewUserImageUploader.update(intraId, userImageFile);
        updateUserRank(user.getId(), currSeason.getId(), userUpdateAdminRequestDto);
    }

    private void updateUserRank(Long userId, Long currSeasonId, UserUpdateAdminRequestDto updateReq) {
        Rank userCurrRank = rankRepository.findByUserIdAndSeasonId(userId, currSeasonId).orElseThrow(() -> new RankNotFoundException());
        RankRedis userCurrRankRedis = rankRedisRepository.findRankByUserId(RedisKeyManager.getHashKey(currSeasonId),
                userId);

        userCurrRank.modifyUserRank(updateReq);
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
        UserImage userImage = userImageAdminRepository.findTopByUserAndIsDeletedOrderByIdDesc(user, false).orElse(null);
        assert userImage != null;
        return userImage.getImageUri();
    }

    @Transactional
    public void deleteUserProfileImage(String intraId) {
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
        UserImage userImage = userImageAdminRepository.findTopByUserAndIsDeletedOrderByIdDesc(user, false).orElseThrow(UserNotFoundException::new);
        userImage.updateIsDeleted(true);
        String userImageUri = getUserImageToString(user);
        user.updateImageUri(userImageUri);
    }

    @Transactional(readOnly = true)
    public UserImageListAdminResponseDto getUserImageDeleteList(Pageable pageable) {
        Page<UserImage> userImagePage = userImageAdminRepository.findAllByIsDeleted(pageable, true);
        Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

        return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
    }
    @Transactional(readOnly = true)
    public UserImageListAdminResponseDto getUserImageDeleteListByIntraId(Pageable pageable, String intraId) {
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
        Page<UserImage> userImagePage = userImageAdminRepository.findAllByUserAndIsDeletedOrderByIdDesc(user.getId(), pageable);
        Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

        return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
    }


    @Transactional(readOnly = true)
    public UserImageListAdminResponseDto getUserImageList(Pageable pageable) {
        Page<UserImage> userImagePage = userImageAdminRepository.findAllChanged(pageable);
        Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

        return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UserImageListAdminResponseDto getUserImageListByIntraId(Pageable pageable, String intraId) {
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
        Page<UserImage> userImagePage = userImageAdminRepository.findAllByUserOrderByIdDesc(pageable, user);
        Page<UserImageAdminDto> userImageAdminDto = userImagePage.map(UserImageAdminDto::new);

        return new UserImageListAdminResponseDto(userImageAdminDto.getContent(), userImageAdminDto.getTotalPages());
    }
}
