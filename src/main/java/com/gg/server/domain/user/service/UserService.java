package com.gg.server.domain.user.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.*;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtRedisRepository jwtRedisRepository;
    private final AuthTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final NotiRepository notiRepository;
    private final GameRepository gameRepository;
    private final RankRedisRepository rankRedisRepository;
    private final SeasonRepository seasonRepository;
    private final PChangeRepository pChangeRepository;
    private final RankRepository rankRepository;

    public String regenerate(String refreshToken) {
        Long userId = jwtRedisRepository.getUserIdByRefToken(refreshToken);
        if (tokenProvider.getTokenClaims(refreshToken) == null)
            throw new TokenNotValidException();
        return tokenProvider.createToken(userId);
    }

    /**
     * @param intraId
     * @return intraId가 포함된 유저들의 intraId를 페이징 관계없이 최대 5개까지 검색하여 List로 return
     */
    @Transactional(readOnly = true)
    public List<String> findByPartOfIntraId(String intraId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("intraId").ascending());
        Page<User> pageUsers = userRepository.findByIntraIdContains(pageable, intraId);
        return pageUsers.getContent().stream().map(user -> user.getIntraId())
                .collect(Collectors.toList());
    }

    /**
     *
     * @param user
     * - event:
     *     - null → 로그인 유저가 잡힌 매칭이 하나도 없을 때
     *     - match → 매칭은 되었으나 게임시작 전일 때
     *     - game → 유저가 게임이 잡혔고 현재 게임중인 경우
     *
     * - currentMatchMode
     *     - normal
     *     - rank
     *     - null -> 매칭이 안잡혔을 때
     */
    @Transactional(readOnly = true)
    public UserLiveResponseDto getUserLiveDetail(UserDto user) {
        int notiCnt = notiRepository.countNotCheckedNotiByUser(user.getId());
        String event = null;
        String currentMatchMode = null;
        Optional<Game> optionalGame = gameRepository.getLatestGameByUser(user.getId());
        if (!optionalGame.isEmpty()) {
            Game latestGame = optionalGame.get();
            if (latestGame.getStatus() == StatusType.END)
                return new UserLiveResponseDto(notiCnt, null, null);
            event = (latestGame.getStatus() == StatusType.BEFORE) ? "match" : "game";
            currentMatchMode = latestGame.getMode().getCode();
        }
        return new UserLiveResponseDto(notiCnt, event, currentMatchMode);
    }

    @Transactional(readOnly = true)
    public UserDetailResponseDto getUserDetail(String targetUserIntraId) {
        User targetUser = userRepository.findByIntraId(targetUserIntraId)
                .orElseThrow(()->new NoSuchElementException("검색된 유저가 없습니다."));
        int currentExp = ExpLevelCalculator.getCurrentLevelMyExp(targetUser.getTotalExp());
        int maxExp = ExpLevelCalculator.getLevelMaxExp(ExpLevelCalculator.getLevel(targetUser.getTotalExp()));
        String statusMessage = getUserStatusMessage(targetUser);
        UserDetailResponseDto responseDto = UserDetailResponseDto.builder()
                .intraId(targetUser.getIntraId())
                .userImageUri(targetUser.getImageUri())
                .racketType(targetUser.getRacketType().getCode())
                .statusMessage(statusMessage)
                .level(ExpLevelCalculator.getLevel(targetUser.getTotalExp()))
                .currentExp(currentExp)
                .maxExp(maxExp)
                .snsNotiOpt(targetUser.getSnsNotiOpt().getCode())
                .build();
        return responseDto;
    }

    private String getUserStatusMessage(User targetUser) {
        Season currentSeason = seasonRepository.findCurrentSeason(LocalDateTime.now())
                .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());
        RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, targetUser.getId());
        if (userRank == null)
            return "";
        else
            return userRank.getStatusMessage();
    }

    @Transactional
    public void updateUser(String racketType, String statusMessage, String snsNotiOpt, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Season currentSeason = seasonRepository.findCurrentSeason(LocalDateTime.now())
                .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        updateRedisRankStatusMessage(statusMessage, userId, user, currentSeason);
        updateRankTable(userId, statusMessage, currentSeason.getId());
        user.update(RacketType.valueOf(racketType), SnsType.valueOf(snsNotiOpt));
    }

    private void updateRankTable(Long userId, String statusMessage, Long seasonId) {
        Rank rank = rankRepository.findByUserIdAndSeasonId(userId, seasonId)
                .orElseThrow(() -> new NoSuchElementException("랭크 테이블에 없는 유저입니다."));
        rank.setStatusMessage(statusMessage);
    }

    private void updateRedisRankStatusMessage(String statusMessage, Long userId, User user, Season currentSeason) {
        String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());

        RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, userId);
        userRank.setStatusMessage(statusMessage);
        rankRedisRepository.updateRankData(hashKey, user.getId(), userRank);
    }

    /**
     *
     * @param userId
     * @param seasonId
     * seasonId == 0 -> current season, else -> 해당 Id를 가진 season의 데이터
     *
     * 기존 쿼리
     * @Query(nativeQuery = true, value = "SELECT * FROM pchange " +
     *             "where game_id in (SELECT id FROM game where season = :season and mode = :mode ) " +
     *             "AND user_id = :intraId ORDER BY id Desc limit :limit")
     *  -> Limit에는 10이 기본으로 들어감
     *
     * @return 유저의 최근 10개의 랭크 경기 기록
     */
    @Transactional(readOnly = true)
    public UserHistoryResponseDto getUserHistory(Long userId, Long seasonId) {
        Season season;
        if (seasonId == 0){
            season = seasonRepository.findCurrentSeason(LocalDateTime.now())
                    .orElseThrow(() -> new NoSuchElementException("해당 시즌이 없습니다."));
        }else{
            season = seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        }
        List<PChange> pChanges = pChangeRepository.findPChangesHistory(userId, season.getId());
        List<UserHistoryData> historyData = pChanges.stream().map(UserHistoryData::new).collect(Collectors.toList());
        Collections.reverse(historyData);
        return new UserHistoryResponseDto(historyData);
    }

    /**
     *
     * @param targetUserIntraId
     * @param seasonId
     * seasonId == 0 -> current season, else -> 해당 Id를 가진 season의 데이터
     * @return
     */
    @Transactional(readOnly = true)
    public UserRankResponseDto getUserRankDetail(String targetUserIntraId, Long seasonId) {
        Season season;
        if (seasonId == 0){
            season = seasonRepository.findCurrentSeason(LocalDateTime.now())
                    .orElseThrow(() -> new NoSuchElementException("해당 시즌이 없습니다."));
        }else{
            season = seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        }
        String ZSetKey = RedisKeyManager.getZSetKey(season.getId());
        String hashKey = RedisKeyManager.getHashKey(season.getId());
        User user = userRepository.findByIntraId(targetUserIntraId)
                .orElseThrow(()->new NoSuchElementException("검색된 유저가 없습니다."));
        Long userRanking = rankRedisRepository.getRankInZSet(ZSetKey,user.getId()) + 1;
        if (userRanking == null)
            return null;
        RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, user.getId());
        double winRate = (userRank.getWins() + userRank.getLosses()) == 0 ? 0 :
                (double)(userRank.getWins() * 10000 / (userRank.getWins() + userRank.getLosses())) / 100;
        return new UserRankResponseDto(userRanking.intValue(), userRank.getPpp(), userRank.getWins(), userRank.getLosses(), winRate);
    }
}
