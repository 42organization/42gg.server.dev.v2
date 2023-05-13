package com.gg.server.admin.game.service;

import com.gg.server.admin.game.dto.GameLogAdminDto;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.data.GameAdminRepository;
import com.gg.server.admin.game.dto.GameTeamAdminDto;
import com.gg.server.admin.team.data.TeamAdminRepository;
import com.gg.server.admin.team.data.TeamUserAdminRepository;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameAdminService {

    private final GameAdminRepository gameAdminRepository;
    private final TeamAdminRepository teamAdminRepository;
    private final TeamUserAdminRepository teamUserAdminRepository;
    @Transactional
    public GameLogListAdminResponseDto findAllGamesByAdmin(Pageable pageable){
        Page<Game> gamePage = gameAdminRepository.findAllByOrderByStartTimeDesc(pageable); //모든 게임 정보 가져오기
        return createGameLogAdminDto(gamePage, pageable);
    }

    @Transactional
    public GameLogListAdminResponseDto createGameLogAdminDto(Page<Game> gamePage, Pageable pageable){
        List<Game> gameList = gamePage.getContent();

        List<GameLogAdminDto> gameLogAdminDtoList = new ArrayList<>();
        for (Game game : gameList) {
            List<GameTeamAdminDto> gameTeamAdminDtoList = new ArrayList<>();

            List<Team> teamList = teamAdminRepository.findAllByGame(game);
            for (Team team : teamList) {
                List<User> userList = teamUserAdminRepository.findUsersByTeamId(team.getId());
                gameTeamAdminDtoList.add(new GameTeamAdminDto(team, userList));
            }
            gameLogAdminDtoList.add(new GameLogAdminDto(game, gameTeamAdminDtoList));
        }
        return new GameLogListAdminResponseDto(gameLogAdminDtoList, gamePage.getTotalPages(), gamePage.getNumber() + 1);
    }
}