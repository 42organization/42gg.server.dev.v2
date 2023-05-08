package com.gg.server.utils;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TestDataUtils {
    private UserRepository userRepository;
    private AuthTokenProvider tokenProvider;
    private NotiRepository notiRepository;
    private SeasonRepository seasonRepository;
    private GameRepository gameRepository;
    private TeamUserRepository teamUserRepository;
    private TeamRepository teamRepository;

    public TestDataUtils(UserRepository userRepository, AuthTokenProvider tokenProvider, NotiRepository notiRepository,
                         SeasonRepository seasonRepository, GameRepository gameRepository, TeamUserRepository teamUserRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.notiRepository = notiRepository;
        this.seasonRepository = seasonRepository;
        this.gameRepository = gameRepository;
        this.teamUserRepository = teamUserRepository;
        this.teamRepository = teamRepository;
    }


    public String getLoginAccessToken() {
        User user = User.builder()
                .eMail("email")
                .intraId("intraId")
                .imageUri("image")
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.USER)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return tokenProvider.createToken(user.getId());
    }

    public User createNewUser(){
        String randomId = UUID.randomUUID().toString();
        User user = User.builder()
                .eMail("email")
                .intraId(randomId)
                .imageUri("image")
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.USER)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return user;
    }

    public User createNewUser(String intraId, String email, String imageUrl, RacketType racketType,
                              SnsType snsType, RoleType roleType){
        User user = User.builder()
                .eMail(email)
                .intraId(intraId)
                .imageUri(imageUrl)
                .racketType(racketType)
                .snsNotiOpt(snsType)
                .roleType(roleType)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return user;
    }

    public void addMockDataUserLiveApi(String event, int notiCnt, String currentMatchMode, Long userId) {
        User curUser = userRepository.findById(userId).get();
        for (int i = 0; i < notiCnt; i++) {
            Noti noti = new Noti(curUser, NotiType.ANNOUNCE, String.valueOf(i), false);
            notiRepository.save(noti);
        }
        LocalDateTime startTime, endTime;
        Season season = createSeason();
        Mode mode = (currentMatchMode == "rank")? Mode.RANK : Mode.NORMAL;
        createGame(curUser, LocalDateTime.now().minusMinutes(100), LocalDateTime.now().minusMinutes(85), season, mode);
        createGame(curUser, LocalDateTime.now().minusMinutes(50), LocalDateTime.now().minusMinutes(35), season, mode);
        LocalDateTime now = LocalDateTime.now();
        if (event == "match"){
            startTime = now.plusMinutes(10);
            endTime = startTime.plusMinutes(15);
            createGame(curUser, startTime, endTime, season, mode);
        }else if (event == "game"){
            startTime = now.minusMinutes(5);
            endTime = startTime.plusMinutes(15);
            createGame(curUser, startTime, endTime, season, mode);
        }
    }

    private void createGame(User curUser, LocalDateTime startTime, LocalDateTime endTime, Season season, Mode mode) {
        LocalDateTime now = LocalDateTime.now();
        Game game;
        if (now.isBefore(startTime))
            game = new Game(season, StatusType.BEFORE, mode, startTime, endTime);
        else if (now.isAfter(startTime) && now.isBefore(endTime))
            game = new Game(season, StatusType.LIVE, mode, startTime, endTime);
        else
            game = new Game(season, StatusType.END, mode, startTime, endTime);
        gameRepository.save(game);
        Team myTeam = new Team(game, 0, false);
        TeamUser teamUser = new TeamUser(myTeam, curUser);
        Team enemyTeam = new Team(game, 0, false);
        User enemyUser = createNewUser();
        TeamUser enemyTeamUser = new TeamUser(enemyTeam, enemyUser);
        teamRepository.save(myTeam);
        teamRepository.save(enemyTeam);
        teamUserRepository.save(teamUser);
        teamUserRepository.save(enemyTeamUser);
    }


    private Season createSeason(){
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMonths(1);
        Season season = new Season("name", startTime, endTime, 1000, 300);
        seasonRepository.save(season);
        return season;
    }
}
