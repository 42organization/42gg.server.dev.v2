package com.gg.server.domain.team.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TeamUserListDto {
    List<TeamUserInfoDto> players;

    public TeamUserListDto(List<TeamUserInfoDto> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "TeamUserListDto{" +
                "players=" + players +
                '}';
    }
}
