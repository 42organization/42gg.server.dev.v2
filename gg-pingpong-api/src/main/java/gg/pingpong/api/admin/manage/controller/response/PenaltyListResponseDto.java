package gg.pingpong.api.admin.manage.controller.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyListResponseDto {
	List<PenaltyUserResponseDto> penaltyList;
	Integer totalPage;
}
