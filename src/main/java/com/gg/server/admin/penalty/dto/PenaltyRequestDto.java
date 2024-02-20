package com.gg.server.admin.penalty.dto;

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
