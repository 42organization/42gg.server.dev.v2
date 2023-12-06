package com.gg.server.domain.tournament.dto;

import com.gg.server.domain.tournament.data.TournamentUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentUserResponseDto {
    private Long userId;
    private String intraId;
    private Boolean isJoined;
    private LocalDateTime registeredDate;

    public TournamentUserResponseDto(TournamentUser tournamentUser) {
        this.userId = tournamentUser.getUser().getId();
        this.intraId = tournamentUser.getUser().getIntraId();
        this.isJoined = tournamentUser.getIsJoined();
        this.registeredDate = tournamentUser.getRegisterTime();
    }
}
