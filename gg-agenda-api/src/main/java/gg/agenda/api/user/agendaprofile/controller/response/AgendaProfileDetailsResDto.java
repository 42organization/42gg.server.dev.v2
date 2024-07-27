package gg.agenda.api.user.agendaprofile.controller.response;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AgendaProfileDetailsResDto {

	private String userIntraId;
	private String userContent;
	private String userGithub;
	private Coalition userCoalition;
	private Location userLocation;
	private int ticketCount;

	public AgendaProfileDetailsResDto(String intraId, AgendaProfile entity, int ticketCount) {
		this.userIntraId = intraId;
		this.userContent = entity.getContent();
		this.userGithub = entity.getGithubUrl();
		this.userCoalition = entity.getCoalition();
		this.userLocation = entity.getLocation();
		this.ticketCount = ticketCount;
	}
}
