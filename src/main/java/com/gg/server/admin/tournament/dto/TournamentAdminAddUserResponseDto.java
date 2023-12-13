package com.gg.server.admin.tournament.dto;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TournamentAdminAddUserResponseDto {
    @NotNull
    private Long userId;

    @NotNull
    private String intraId;

    @NotNull
    private Boolean isJoined;

}
