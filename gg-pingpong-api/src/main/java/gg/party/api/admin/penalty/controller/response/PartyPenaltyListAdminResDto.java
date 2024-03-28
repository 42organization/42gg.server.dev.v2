package gg.party.api.admin.penalty.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyPenaltyListAdminResDto {
	private List<PartyPenaltyAdminResDto> penaltyList;
	private int totalPage;

	public PartyPenaltyListAdminResDto(List<PartyPenaltyAdminResDto> penaltyList, int totalPage) {
		this.penaltyList = penaltyList;
		this.totalPage = totalPage;
	}
}
