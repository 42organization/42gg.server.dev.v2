package gg.agenda.api.user.agendaprofile.controller.response;

import java.net.URL;
import java.util.List;

import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraAchievement;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfile;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class MyAgendaProfileDetailsResDto {

	private String userIntraId;
	private String userContent;
	private String userGithub;
	private Coalition userCoalition;
	private Location userLocation;
	private int ticketCount;
	private URL imageUrl;
	private List<IntraAchievement> achievements;

	@Builder
	public MyAgendaProfileDetailsResDto(String userIntraId, String userContent, String userGithub,
		Coalition userCoalition, Location userLocation, int ticketCount, URL imageUrl,
		List<IntraAchievement> achievements) {
		this.userIntraId = userIntraId;
		this.userContent = userContent;
		this.userGithub = userGithub;
		this.userCoalition = userCoalition;
		this.userLocation = userLocation;
		this.ticketCount = ticketCount;
		this.imageUrl = imageUrl;
		this.achievements = achievements;
	}

	public static MyAgendaProfileDetailsResDto toDto(AgendaProfile profile, int ticketCount, IntraProfile intraProfile) {
		return MyAgendaProfileDetailsResDto.builder()
			.userIntraId(profile.getIntraId())
			.userContent(profile.getContent())
			.userGithub(profile.getGithubUrl())
			.userCoalition(profile.getCoalition())
			.userLocation(profile.getLocation())
			.ticketCount(ticketCount)
			.imageUrl(intraProfile.getImageUrl())
			.achievements(intraProfile.getAchievements())
			.build();
	}
}
