package gg.pingpong.api.admin.penalty.dto;

import javax.validation.constraints.PositiveOrZero;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
