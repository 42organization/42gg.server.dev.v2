package com.gg.server.admin.penalty.dto;

import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyRequestDto {
    @Length(max = 30)
    private String intraId;
    @PositiveOrZero
    private Integer penaltyTime;
    private String reason;
}
