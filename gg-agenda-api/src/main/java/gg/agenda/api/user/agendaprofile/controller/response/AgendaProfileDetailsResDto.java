package gg.agenda.api.user.agendaprofile.controller.response;

import java.net.URL;
import java.util.List;

import org.mapstruct.Mapper;

import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraAchievement;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfile;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendaProfileDetailsResDto {
	private String userIntraId;
	private String userContent;
	private String userGithub;
	private Coalition userCoalition;
	private Location userLocation;
	private URL imageUrl;
	private List<IntraAchievement> achievements;

	@Builder
	public AgendaProfileDetailsResDto(String userIntraId, String userContent, String userGithub,
		Coalition userCoalition, Location userLocation, URL imageUrl,
		List<IntraAchievement> achievements) {
		this.userIntraId = userIntraId;
		this.userContent = userContent;
		this.userGithub = userGithub;
		this.userCoalition = userCoalition;
		this.userLocation = userLocation;
		this.imageUrl = imageUrl;
		this.achievements = achievements;
	}

	public static AgendaProfileDetailsResDto toDto(AgendaProfile profile, IntraProfile intraProfile) {
		return AgendaProfileDetailsResDto.builder()
			.userIntraId(profile.getIntraId())
			.userContent(profile.getContent())
			.userGithub(profile.getGithubUrl())
			.userCoalition(profile.getCoalition())
			.userLocation(profile.getLocation())
			.imageUrl(intraProfile.getImageUrl())
			.achievements(intraProfile.getAchievements())
			.build();
	}
}
