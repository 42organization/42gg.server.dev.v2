package com.gg.server.admin.user.service;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.admin.user.dto.UserDetailAdminResponseDto;
import com.gg.server.admin.user.dto.UserSearchAdminDto;
import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.admin.user.dto.UserUpdateAdminRequestDto;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RankNotFoundException;
import com.gg.server.domain.rank.exception.RankUpdateException;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.utils.aws.AsyncNewUserImageUploader;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserAdminService {

    private final UserAdminRepository userAdminRepository;
    private final SeasonAdminRepository seasonAdminRepository;
    private final RankRepository rankRepository;
    private final RankRedisRepository rankRedisRepository;
    private final RankRedisAdminService rankRedisAdminService;
    private final AsyncNewUserImageUploader asyncNewUserImageUploader;

    @Transactional(readOnly = true)
    public UserSearchAdminResponseDto searchAll(Pageable pageable) {
        Page<User> userPage = userAdminRepository.findAll(pageable);
        List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages(), userPage.getNumber() + 1);
    }

    /* 문자열을 포함하는 intraId를 가진 유저 찾기 */
    @Transactional(readOnly = true)
    public UserSearchAdminResponseDto findByPartsOfIntraId(String intraId, Pageable pageable) {
        Page<User> userPage = userAdminRepository.findByIntraIdContains(pageable, intraId);
        List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages(), userPage.getNumber() + 1);
    }

    @Transactional(readOnly = true)
    public UserDetailAdminResponseDto getUserDetailByIntraId(String intraId) {
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(() -> new NotFoundException("못찾음"));//에러코드 수정 필요
        Season currSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now()).orElseThrow(() -> new NotFoundException("못찾음"));// 에러코드 수정 필요
        RankRedis userCurrRank = rankRedisRepository.findRankByUserId(RedisKeyManager.getHashKey(currSeason.getId()),
                user.getId());
        return new UserDetailAdminResponseDto(user, userCurrRank);
    }

    @Transactional
    public void updateUserDetail(String intraId,
                                 UserUpdateAdminRequestDto userUpdateAdminRequestDto,
                                 MultipartFile userImageFile) throws IOException{
        Season currSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now()).orElseThrow(() -> new SeasonNotFoundException("못찾음", ErrorCode.SEASON_NOT_FOUND));//에러코드 수정 필요
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(() -> new NotFoundException("못찾음"));//에러코드 수정 필요

        user.modifyUserDetail(userUpdateAdminRequestDto);
        asyncNewUserImageUploader.update(intraId, userImageFile);
        updateUserRank(user.getId(), currSeason.getId(), userUpdateAdminRequestDto);
    }

    private void updateUserRank(Long userId, Long currSeasonId, UserUpdateAdminRequestDto updateReq) {
        Rank userCurrRank = rankRepository.findByUserIdAndSeasonId(userId, currSeasonId).orElseThrow(() -> new RankNotFoundException("못찾음", ErrorCode.RANK_NOT_FOUND));//에러코드 수정 필요
        RankRedis userCurrRankRedis = rankRedisRepository.findRankByUserId(RedisKeyManager.getHashKey(currSeasonId),
                userId);

        userCurrRank.modifyUserRank(updateReq);
        userCurrRank.setStatusMessage(updateReq.getStatusMessage());

        userCurrRankRedis.updateRank(updateReq.getPpp(),
                updateReq.getWins(),
                updateReq.getLosses());
        userCurrRankRedis.setStatusMessage(updateReq.getStatusMessage());
        rankRedisAdminService.updateRankUser(RedisKeyManager.getHashKey(currSeasonId),
                RedisKeyManager.getZSetKey(currSeasonId),
                userId, userCurrRankRedis);
    }
    private String getUserStatusMessage(User targetUser) {
        Season currentSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now())
                .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());
        RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, targetUser.getId());
        if (userRank == null)
            return "";
        else
            return userRank.getStatusMessage();
    }

//    @Transactional( noRollbackFor = { RankUpdateException.class })
//    public Boolean updateUserDetailByAdmin(UserUpdateRequestAdmintDto updateRequestDto, MultipartFile multipartFile) throws IOException {
//        User user = userAdminRepository.findById(updateRequestDto.getUserId()).orElseThrow();
//        user.setEMail(updateRequestDto.getEmail());
//        user.setRacketType(updateRequestDto.getRacketType());
//        user.setStatusMessage(updateRequestDto.getStatusMessage());
//        user.setRoleType(RoleType.of(updateRequestDto.getRoleType()));
//        userAdminRepository.save(user);
//
//        if (multipartFile != null) {
//            asyncNewUserImageUploader.update(updateRequestDto.getIntraId(), multipartFile);
//        }
//
//        RankRedis rankRedis = rankRedisRepository.findRank(redisKeyManager.getCurrentRankKey(), user.getId());
//        if (rankRedis == null) {
//            throw new RankUpdateException("RK001");
//        }
//
//        rankRedis.setPpp(updateRequestDto.getPpp());
//        rankRedis.setWins(updateRequestDto.getWins());
//        rankRedis.setLosses(updateRequestDto.getLosses());
//        rankRedis.setStatusMessage(updateRequestDto.getStatusMessage());
//        rankRedis.setRacketType(updateRequestDto.getRacketType());
//        Integer wins = updateRequestDto.getWins();
//        Integer losses = updateRequestDto.getLosses();
//        rankRedis.setWinRate((wins + losses) == 0 ? 0 : (double)(wins * 10000 / (wins + losses)) / 100);
//
//        Season season = seasonRepository.findFirstByModeOrModeAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Mode.BOTH, Mode.RANK, LocalDateTime.now()).orElseThrow(() -> new BusinessException("E0001"));
//        String redisSeason = redisKeyManager.getSeasonKey(season.getId().toString(), season.getSeasonName());
//        RedisRankUpdateDto redisRankUpdateDto = RedisRankUpdateDto.builder()
//                .userRank(rankRedis)
//                .userId(rankRedis.getId())
//                .seasonKey(redisSeason)
//                .build();
//        rankRedisRepository.updateRank(redisRankUpdateDto);
//
//        if (updateRequestDto.getWins() + updateRequestDto.getLosses() > 0) {
//            RankKeyGetDto rankKeyGetDto = RankKeyGetDto.builder().seasonId(season.getId()).seasonName(season.getSeasonName()).build();
//            String curRankingKey = redisKeyManager.getRankingKeyBySeason(rankKeyGetDto, GameType.SINGLE);
//            RedisRankingUpdateDto redisRankingUpdateDto = RedisRankingUpdateDto.builder()
//                    .rankingKey(curRankingKey)
//                    .rank(rankRedis)
//                    .ppp(rankRedis.getPpp())
//                    .build();
//            rankRedisRepository.updateRanking(redisRankingUpdateDto);
//        }
//        return true;
//    }
}
