package com.gg.server.admin.tournament.dto;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TournamentAdminAddUserRequestDto {
    @NotNull(message = "intraId가 필요합니다.")
    private String intraId;
}
