package com.gg.server.utils;

import com.gg.server.admin.tournament.dto.TournamentAdminUpdateRequestDto;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.controller.dto.GameInfoDto;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataUtils {
    private final UserRepository userRepository;
    private final AuthTokenProvider tokenProvider;
    private final NotiRepository notiRepository;
    private final SeasonRepository seasonRepository;
    private final GameRepository gameRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final RankRedisRepository redisRepository;
    private final PChangeRepository pChangeRepository;
    private final RankRepository rankRepository;
    private final TierRepository tierRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentGameRepository tournamentGameRepository;

    public String getLoginAccessToken() {
        User user = User.builder()
                .eMail("email")
                .intraId("intraId")
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.USER)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return tokenProvider.createToken(user.getId());
    }

    public String getAdminLoginAccessToken() {
        User user = User.builder()
                .eMail("email")
                .intraId("intraId")
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.ADMIN)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return tokenProvider.createToken(user.getId());
    }

    public User createNewUser(){
        String randomId = UUID.randomUUID().toString().substring(0, 30);
        User user = User.builder()
                .eMail("email")
                .intraId(randomId)
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.USER)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return user;
    }

    public User createNewUser(String intraId, String email, RacketType racketType,
                              SnsType snsType, RoleType roleType){
        User user = User.builder()
                .eMail(email)
                .intraId(intraId)
                .racketType(racketType)
                .snsNotiOpt(snsType)
                .roleType(roleType)
                .totalExp(0)
                .build();
        userRepository.save(user);
        return user;
    }

    public User createNewUser(int totalExp){
        String randomId = UUID.randomUUID().toString().substring(0, 30);
        User user = User.builder()
                .eMail("email")
                .intraId(randomId)
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.USER)
                .totalExp(totalExp)
                .build();
        userRepository.save(user);
        return user;
    }

    public GameInfoDto addMockDataUserLiveApi(String event, int notiCnt, String currentMatchMode, Long userId) {
        User curUser = userRepository.findById(userId).get();
        for (int i = 0; i < notiCnt; i++) {
            Noti noti = new Noti(curUser, NotiType.ANNOUNCE, String.valueOf(i), false);
            notiRepository.save(noti);
        }
        LocalDateTime startTime, endTime;
        Season season = createSeason();
        createUserRank(curUser, "testUserMessage", season);
        Mode mode = (currentMatchMode == "RANK")? Mode.RANK : Mode.NORMAL;
        createGame(curUser, LocalDateTime.now().minusMinutes(100), LocalDateTime.now().minusMinutes(85), season, mode);
        createGame(curUser, LocalDateTime.now().minusMinutes(50), LocalDateTime.now().minusMinutes(35), season, mode);
        LocalDateTime now = LocalDateTime.now();
        if (event.equals("match")){
            startTime = now.plusMinutes(10);
            endTime = startTime.plusMinutes(15);
            return createGame(curUser, startTime, endTime, season, mode);
        }else if (event.equals("game")){
            startTime = now.minusMinutes(5);
            endTime = startTime.plusMinutes(15);
            return createGame(curUser, startTime, endTime, season, mode);
        }
        return null;
    }

    public GameInfoDto createGame(User curUser, LocalDateTime startTime, LocalDateTime endTime, Season season, Mode mode) {
        LocalDateTime now = LocalDateTime.now();
        Game game;
        if (now.isBefore(startTime))
            game = new Game(season, StatusType.BEFORE, mode, startTime, endTime);
        else if (now.isAfter(startTime) && now.isBefore(endTime))
            game = new Game(season, StatusType.LIVE, mode, startTime, endTime);
        else
            game = new Game(season, StatusType.END, mode, startTime, endTime);
        gameRepository.save(game);
        Team myTeam = new Team(game, -1, false);
        TeamUser teamUser = new TeamUser(myTeam, curUser);
        Team enemyTeam = new Team(game, -1, false);
        User enemyUser = createNewUser();
        createUserRank(curUser, "statusMessage", season);
        createUserRank(enemyUser, "enemyUserMeassage", season);
        TeamUser enemyTeamUser = new TeamUser(enemyTeam, enemyUser);
        teamRepository.save(myTeam);
        teamRepository.save(enemyTeam);
        teamUserRepository.save(teamUser);
        teamUserRepository.save(enemyTeamUser);

        return new GameInfoDto(game.getId(), myTeam.getId(), curUser.getId(), enemyTeam.getId(), enemyUser.getId());
    }


    public Season createSeason(){
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMonths(1);
        Season season = seasonRepository.findCurrentSeason(LocalDateTime.now()).orElse(null);
        if (season == null)
            season = new Season("name", startTime, endTime, 1000, 300);
        seasonRepository.save(season);
        return season;
    }

    public void createUserRank(User newUser, String statusMessage, Season season) {
        if (rankRepository.findByUserIdAndSeasonId(newUser.getId(), season.getId()).isPresent())
            return ;
        String zSetKey = RedisKeyManager.getZSetKey(season.getId());
        String hashKey = RedisKeyManager.getHashKey(season.getId());
        redisRepository.addRankData(hashKey, newUser.getId(),
                new RankRedis(newUser.getId(), "aa", season.getStartPpp(), 0, 0, statusMessage, "https://42gg-public-image.s3.ap-northeast-2.amazonaws.com/images/nheo.jpeg", "#000000"));
        Rank userRank = Rank.builder()
                        .user(newUser)
                        .season(season)
                        .ppp(season.getStartPpp())
                        .wins(0)
                        .losses(0)
                        .statusMessage(statusMessage)
                        .build();
        rankRepository.save(userRank);
    }

    public void createUserRank(User newUser, String statusMessage, Season season, Tier tier) {
        if (rankRepository.findByUserIdAndSeasonId(newUser.getId(), season.getId()).isPresent())
            return ;
        String zSetKey = RedisKeyManager.getZSetKey(season.getId());
        String hashKey = RedisKeyManager.getHashKey(season.getId());
        redisRepository.addRankData(hashKey, newUser.getId(),
                new RankRedis(newUser.getId(), "aa", season.getStartPpp(), 0, 0, statusMessage, "https://42gg-public-image.s3.ap-northeast-2.amazonaws.com/images/nheo.jpeg", "#000000"));
        Rank userRank = Rank.builder()
                .user(newUser)
                .season(season)
                .ppp(season.getStartPpp())
                .wins(0)
                .losses(0)
                .statusMessage(statusMessage)
                .tier(tier)
                .build();
        rankRepository.save(userRank);
    }

    public void createUserRank(User newUser, String statusMessage, Season season, int ppp) {
        String zSetKey = RedisKeyManager.getZSetKey(season.getId());
        String hashKey = RedisKeyManager.getHashKey(season.getId());
        Tier tier = tierRepository.getById(1L);
        redisRepository.addToZSet(zSetKey, newUser.getId(), ppp);
        redisRepository.addRankData(hashKey, newUser.getId(),
                new RankRedis(newUser.getId(), "aa", ppp, 1, 0, statusMessage, "https://42gg-public-image.s3.ap-northeast-2.amazonaws.com/images/nheo.jpeg", "#000000"));
        Rank userRank = Rank.builder()
                .user(newUser)
                .season(season)
                .ppp(ppp)
                .wins(1)
                .losses(0)
                .statusMessage(statusMessage)
                .tier(tier)
                .build();
        rankRepository.save(userRank);
    }

    public void createMockMatch(User newUser, Season season, LocalDateTime startTime, LocalDateTime endTime) {
        Game game = new Game(season, StatusType.END, Mode.RANK, startTime, endTime);
        gameRepository.save(game);
        Team myTeam = new Team(game, 0, false);
        TeamUser teamUser = new TeamUser(myTeam, newUser);
        Team enemyTeam = new Team(game, 0, false);
        User enemyUser = createNewUser();
        TeamUser enemyTeamUser = new TeamUser(enemyTeam, enemyUser);
        teamRepository.save(myTeam);
        teamRepository.save(enemyTeam);
        teamUserRepository.save(teamUser);
        teamUserRepository.save(enemyTeamUser);

        PChange pChange1 = new PChange(game, newUser, 1100, true);
        PChange pChange2 = new PChange(game, enemyUser, 900, true);

        pChangeRepository.save(pChange1);
        pChangeRepository.save(pChange2);
    }

    /**
     * 테스트용 토너먼트 반환. 매개변수 값들만 초기화
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    public Tournament createTournament(LocalDateTime startTime, LocalDateTime endTime, TournamentStatus status) {
        Tournament tournament = Tournament.builder()
            .title("title")
            .contents("contents")
            .startTime(startTime)
            .endTime(endTime)
            .type(TournamentType.ROOKIE)
            .status(status).build();
        return  tournamentRepository.save(tournament);
    }

    /**
     * 테스트용 토너먼트 RequestDto 반환. 매개변수 값들만 초기화
     * @param startTime
     * @param endTime
     * @param type
     * @return
     */
    public TournamentAdminUpdateRequestDto createUpdateRequestDto(LocalDateTime startTime, LocalDateTime endTime, TournamentType type) {
        return new TournamentAdminUpdateRequestDto(
            "title",
            "contents",
            startTime,
            endTime,
            type);
    }

    /**
     * 테스트용 토너먼트 게임 리스트 반환. 매개변수 값들만 초기화
     * @param tournament
     * @param cnt
     * @return
     */
    public List<TournamentGame> createTournamentGameList(Tournament tournament, int cnt) {
        List<TournamentGame> tournamentGameList = new ArrayList<>();
        TournamentRound [] values = TournamentRound.values();

        while (--cnt >= 0) {
            tournamentGameList.add(new TournamentGame(null, tournament, values[cnt]));
        }
        return tournamentGameRepository.saveAll(tournamentGameList);
    }
}
