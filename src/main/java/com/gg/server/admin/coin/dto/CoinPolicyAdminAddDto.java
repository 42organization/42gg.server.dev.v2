package com.gg.server.admin.coin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoinPolicyAdminAddDto {
    @NotNull(message = "plz. not null attendance")
    @PositiveOrZero(message = "plz. attendance PositiveOrZero")
    private int attendance;

    @NotNull(message = "plz. not null normal")
    @PositiveOrZero(message = "plz. normal PositiveOrZero")
    private int normal;

    @NotNull(message = "plz. not null rankWin")
    @PositiveOrZero(message = "plz. rankWin PositiveOrZero")
    private int rankWin;

    @NotNull(message = "plz. not null rankLose")
    @PositiveOrZero(message = "plz. rankLose PositiveOrZero")
    private int rankLose;
}