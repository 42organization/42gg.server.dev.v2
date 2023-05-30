package com.gg.server.admin.game.dto;

import com.gg.server.global.dto.PageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
public class GameLogAdminRequestDto extends PageRequestDto {
    private Long seasonId;

    public GameLogAdminRequestDto(Integer page, Integer size, Long seasonId) {
        super(page, size);
        this.seasonId = seasonId;
    }
}
