package com.gg.server.admin.game.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameTeamAdminDto {
    private String intraId1;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String intraId2;  //복식일 경우에만 있음
    private Long teamId;
    private Integer score;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean win;

    public GameTeamAdminDto(Team team, List<User> userList) {
        this.intraId1 = userList.get(0).getIntraId();
        if (userList.size() > 1)
            this.intraId2 = userList.get(1).getIntraId();
        else
            this.intraId2 = null;
        this.teamId = team.getId();
        this.score = team.getScore();
        this.win = Optional.ofNullable(team.getWin()).orElse(null);
    }
}
