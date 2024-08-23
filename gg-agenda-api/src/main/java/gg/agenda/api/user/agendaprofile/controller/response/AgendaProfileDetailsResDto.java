package gg.agenda.api.user.agendaprofile.controller.response;

import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfile;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import lombok.Builder;
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
	private IntraProfile intraProfile;

	@Builder
	public AgendaProfileDetailsResDto(String userIntraId, String userContent, String userGithub,
		Coalition userCoalition, Location userLocation, int ticketCount, IntraProfile intraProfile) {
		this.userIntraId = userIntraId;
		this.userContent = userContent;
		this.userGithub = userGithub;
		this.userCoalition = userCoalition;
		this.userLocation = userLocation;
		this.ticketCount = ticketCount;
		this.intraProfile = intraProfile;
	}

	public static AgendaProfileDetailsResDto toDto(AgendaProfile profile, int ticketCount, IntraProfile intraProfile) {
		return AgendaProfileDetailsResDto.builder()
			.userIntraId(profile.getIntraId())
			.userContent(profile.getContent())
			.userGithub(profile.getGithubUrl())
			.userCoalition(profile.getCoalition())
			.userLocation(profile.getLocation())
			.ticketCount(ticketCount)
			.intraProfile(intraProfile)
			.build();
	}
}
