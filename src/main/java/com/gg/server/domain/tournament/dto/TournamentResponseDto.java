package com.gg.server.domain.tournament.dto;


import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.dto.UserImageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentResponseDto {

    private Long tournamentId;
    private String title;
    private String contents;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TournamentType type;
    private TournamentStatus status;
    private String winnerIntraId;
    private String winnerImageUrl;
    private int playerCnt;

    public TournamentResponseDto (Tournament tournament, UserImageDto winner, int playerCnt) {
        this.tournamentId = tournament.getId();
        this.title = tournament.getTitle();
        this.contents = tournament.getContents();
        this.startTime = tournament.getStartTime();
        this.endTime = tournament.getEndTime();
        this.type = tournament.getType();
        this.status = tournament.getStatus();
        this.winnerIntraId = winner.getIntraId();
        this.winnerImageUrl = winner.getImageUri();
        this.playerCnt = playerCnt;
    }

    public void update_player_cnt(int playerCnt) {
        this.playerCnt = playerCnt;
    }
}
