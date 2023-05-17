package com.gg.server.admin.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameLogAdminRequestDto {
    private Long seasonId;
    @NotNull
    @Positive(message = "Positive vaild fail")
    private int page;
    @Size(min=1)
    private int size = 20;
}
