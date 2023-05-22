package com.gg.server.domain.game.dto;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.team.dto.MatchTeamsInfoDto;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.GameDBException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class GameTeamInfo {
    private Mode mode;
    private Long gameId;
    private LocalDateTime startTime;
    private Boolean isScoreExist;
    private MatchTeamsInfoDto matchTeamsInfo;

    public GameTeamInfo(List<GameTeamUserInfo> infos, Long userId) {
        this.mode = infos.get(0).getMode();
        this.gameId = infos.get(0).getGameId();
        this.startTime = infos.get(0).getStartTime();
        Long myTeamId = null;
        for (GameTeamUserInfo info :
                infos) {
            if (info.getScore() != 0) {
                this.isScoreExist = true;
            }
            if (!this.mode.equals(info.getMode()) || !this.gameId.equals(info.getGameId()) || !this.startTime.equals(info.getStartTime())) {
                throw new GameDBException("DB 정보 오류", ErrorCode.INTERNAL_SERVER_ERR);
            }
            if (info.getUserId().equals(userId))
                myTeamId = info.getTeamId();
        }
        this.matchTeamsInfo = new MatchTeamsInfoDto(infos, myTeamId);
    }
}
