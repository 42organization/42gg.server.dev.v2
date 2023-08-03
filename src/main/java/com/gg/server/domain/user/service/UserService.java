package com.gg.server.domain.user.service;

import com.gg.server.domain.coin.data.CoinHistory;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.pchange.exception.PChangeNotExistException;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.exception.RedisDataNotFoundException;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.rank.service.RankFindService;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.*;
import com.gg.server.domain.user.exception.UserAlreadyAttendanceException;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.SnsType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserFindService userFindService;
    private final UserRepository userRepository;
    private final NotiRepository notiRepository;
    private final GameRepository gameRepository;
    private final RankRedisRepository rankRedisRepository;
    private final SeasonFindService seasonFindService;
    private final PChangeRepository pChangeRepository;
    private final RankFindService rankFindService;
    private final RedisMatchUserRepository redisMatchUserRepository;
    private final CoinHistoryRepository coinHistoryRepository;
    private final CoinPolicyRepository coinPolicyRepository;

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
     * @param user - event:
     *             - null → 로그인 유저가 잡힌 매칭이 하나도 없을 때
     *             - match → 매칭은 되었으나 게임시작 전일 때 or 매칭중인 경우
     *             - game → 유저가 게임이 잡혔고 현재 게임중인 경우
     *             <p>
     *             - currentMatchMode
     *             - normal
     *             - rank
     *             - null -> 매칭이 안잡혔을 때 or 게임 전
     */
    @Transactional()
    public UserLiveResponseDto getUserLiveDetail(UserDto user) {
        int notiCnt = notiRepository.countNotCheckedNotiByUser(user.getId());
        Optional<Game> optionalGame = gameRepository.getLatestGameByUser(user.getId());
        int userMatchCnt = redisMatchUserRepository.countMatchTime(user.getId());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            if (game.getStatus() == StatusType.LIVE || game.getStatus() == StatusType.WAIT)
                return new UserLiveResponseDto(notiCnt, "game", game.getMode(), game.getId());
            else if (game.getStatus() == StatusType.END) {
                PChange userPChange = pChangeRepository.findPChangeByUserIdAndGameId(user.getId(), game.getId()).orElseThrow(() -> new PChangeNotExistException());
                if (userPChange.getIsChecked() == false) {
                    userPChange.checkPChange();
                    return new UserLiveResponseDto(notiCnt, "game", game.getMode(), game.getId());
                }
            }

            if (game.getStatus() == StatusType.BEFORE)
                return new UserLiveResponseDto(notiCnt, "match", null, null);
        }
        if (userMatchCnt > 0) {
            return new UserLiveResponseDto(notiCnt, "match", null, null);
        }
        return new UserLiveResponseDto(notiCnt, null, null, null);
    }

    @Transactional(readOnly = true)
    public UserDetailResponseDto getUserDetail(String targetUserIntraId) {
        User targetUser = userFindService.findByIntraId(targetUserIntraId);
        String statusMessage = userFindService.getUserStatusMessage(targetUser);
        return new UserDetailResponseDto(targetUser, statusMessage);
    }

    @Transactional
    public void updateUser(RacketType racketType, String statusMessage, SnsType snsNotiOpt, String intraId) {
        User user = userFindService.findByIntraId(intraId);
        Season currentSeason = seasonFindService.findCurrentSeason(LocalDateTime.now());
        updateRedisRankStatusMessage(statusMessage, user, currentSeason);
        updateRankTableStatusMessage(user.getId(), statusMessage, currentSeason.getId());
        user.updateTypes(racketType, snsNotiOpt);
    }

    private void updateRankTableStatusMessage(Long userId, String statusMessage, Long seasonId) {
        Rank rank = rankFindService.findByUserIdAndSeasonId(userId, seasonId);
        rank.setStatusMessage(statusMessage);
    }

    private void updateRedisRankStatusMessage(String statusMessage, User user, Season currentSeason) {
        String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());

        RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, user.getId());
        userRank.setStatusMessage(statusMessage);
        rankRedisRepository.updateRankData(hashKey, user.getId(), userRank);
    }

    /**
     * @param intraId
     * @param seasonId seasonId == 0 -> current season, else -> 해당 Id를 가진 season의 데이터
     *                 <p>
     *                 기존 쿼리
     * @return 유저의 최근 10개의 랭크 경기 기록
     * @Query(nativeQuery = true, value = "SELECT * FROM pchange " +
     * "where game_id in (SELECT id FROM game where season = :season and mode = :mode ) " +
     * "AND user_id = :intraId ORDER BY id Desc limit :limit")
     * -> Limit에는 10이 기본으로 들어감
     */
    @Transactional(readOnly = true)
    public UserHistoryResponseDto getUserHistory(String intraId, Long seasonId) {
        Season season;
        if (seasonId == 0) {
            season = seasonFindService.findCurrentSeason(LocalDateTime.now());
        } else {
            season = seasonFindService.findSeasonById(seasonId);
        }
        List<PChange> pChanges = pChangeRepository.findPChangesHistory(intraId, season.getId());
        List<UserHistoryData> historyData = pChanges.stream().map(UserHistoryData::new).collect(Collectors.toList());
        Collections.reverse(historyData);
        return new UserHistoryResponseDto(historyData);
    }

    /**
     * @param targetUserIntraId
     * @param seasonId          seasonId == 0 -> current season, else -> 해당 Id를 가진 season의 데이터
     * @return
     */
    @Transactional(readOnly = true)
    public UserRankResponseDto getUserRankDetail(String targetUserIntraId, Long seasonId) {
        Season season;
        if (seasonId == 0) {
            season = seasonFindService.findCurrentSeason(LocalDateTime.now());
        } else {
            season = seasonFindService.findSeasonById(seasonId);
        }
        String ZSetKey = RedisKeyManager.getZSetKey(season.getId());
        String hashKey = RedisKeyManager.getHashKey(season.getId());
        User user = userFindService.findByIntraId(targetUserIntraId);
        try {
            Long userRanking = rankRedisRepository.getRankInZSet(ZSetKey, user.getId());
            userRanking += 1;
            RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, user.getId());
            double winRate = (double) (userRank.getWins() * 10000 / (userRank.getWins() + userRank.getLosses())) / 100;
            return new UserRankResponseDto(userRanking.intValue(), userRank.getPpp(), userRank.getWins(), userRank.getLosses(), winRate);
        } catch (RedisDataNotFoundException ex) {
            return new UserRankResponseDto(-1, season.getStartPpp(), 0, 0, 0);
        } catch (ArithmeticException ex2) {
            return new UserRankResponseDto(-1, season.getStartPpp(), 0, 0, 0);
        }
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User" + userId));
    }

    @Transactional
    public void deleteKakaoId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        user.updateKakaoId(null);
    }

    @Transactional(readOnly = true)
    public UserImageResponseDto getRankedUserImagesByPPP(Long seasonId) {
        Season targetSeason;
        if (seasonId == 0)
            targetSeason = seasonFindService.findCurrentSeason(LocalDateTime.now());
        else
            targetSeason = seasonFindService.findSeasonById(seasonId);
        try {
            String zSetKey = RedisKeyManager.getZSetKey(targetSeason.getId());
            List<Long> userIds = rankRedisRepository.getUserIdsByRangeFromZSet(zSetKey, 0, 2);
            List<User> users = userRepository.findUsersByIdIn(userIds);
            List<UserImageDto> userImages = new ArrayList<>();
            userIds.forEach(userId -> {
                User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(UserNotFoundException::new);
                userImages.add(new UserImageDto(user.getIntraId(), user.getImageUri()));
            });
            return new UserImageResponseDto(userImages);
        } catch (RedisDataNotFoundException ex) {
            return new UserImageResponseDto(new ArrayList<>());
        }
    }

    public UserImageResponseDto getRankedUserImagesByExp(PageRequest pageRequest) {
        List<User> users = userRepository.findAll(pageRequest).getContent();
        List<UserImageDto> userImages = new ArrayList<>();
        for (User user : users) {
            userImages.add(new UserImageDto(user.getIntraId(), user.getImageUri()));
        }
        return new UserImageResponseDto(userImages);
    }
  
    @Transactional()
    public void updateTextColor(Long userId, String textColor) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.updateTextColor(textColor);
    }

    @Transactional
    public UserAttendanceResponseDto attendUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User" + userId));
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        if (coinHistoryRepository.existsCoinHistoryByUserAndHistoryAndCreatedAtToday(user, "ATTENDANCE", startOfDay, endOfDay))
            throw new UserAlreadyAttendanceException();
        int plus = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getAttendance();
        CoinHistory coinHistory = new CoinHistory(user, "ATTENDANCE", plus);
        coinHistoryRepository.save(coinHistory);
        int beforeCoin = user.getGgCoin();
        user.addGgCoin(plus);
        return new UserAttendanceResponseDto(beforeCoin, user.getGgCoin(), plus);
    }
}