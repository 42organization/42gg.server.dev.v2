package gg.agenda.api.user.agendaprofile.controller.response;

import org.mapstruct.Mapper;

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
	private IntraProfile intraProfile;

	@Builder
	public AgendaProfileDetailsResDto(String userIntraId, String userContent, String userGithub,
		Coalition userCoalition,
		Location userLocation, IntraProfile intraProfile) {
		this.userIntraId = userIntraId;
		this.userContent = userContent;
		this.userGithub = userGithub;
		this.userCoalition = userCoalition;
		this.userLocation = userLocation;
		this.intraProfile = intraProfile;
	}

	public static AgendaProfileDetailsResDto toDto(AgendaProfile profile, IntraProfile intraProfile) {
		return AgendaProfileDetailsResDto.builder()
			.userIntraId(profile.getIntraId())
			.userContent(profile.getContent())
			.userGithub(profile.getGithubUrl())
			.userCoalition(profile.getCoalition())
			.userLocation(profile.getLocation())
			.intraProfile(intraProfile)
			.build();
	}
}
