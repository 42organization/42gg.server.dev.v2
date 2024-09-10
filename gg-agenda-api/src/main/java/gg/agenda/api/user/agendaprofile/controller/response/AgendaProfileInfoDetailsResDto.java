package gg.agenda.api.user.agendaprofile.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AgendaProfileInfoDetailsResDto {
	private String intraId;
	private Boolean isAdmin;

	public AgendaProfileInfoDetailsResDto(String intraId, Boolean isAdmin) {
		this.intraId = intraId;
		this.isAdmin = isAdmin;
	}
}
