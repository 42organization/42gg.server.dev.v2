package com.gg.server.admin.coin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoinPolicyAdminAddDto {
    @NotNull(message = "plz. attendance")
    private int attendance;

    @NotNull(message = "plz. normal")
    private int normal;

    @NotNull(message = "plz. rankWin")
    private int rankWin;

    @NotNull(message = "plz. rankLose")
    private int rankLose;
}