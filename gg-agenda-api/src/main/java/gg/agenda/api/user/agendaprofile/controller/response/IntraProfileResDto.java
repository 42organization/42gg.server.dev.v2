package gg.agenda.api.user.agendaprofile.controller.response;

import java.net.URL;
import java.util.List;

import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraAchievement;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class IntraProfileResDto {
	private String intraId;
	private URL imageUrl;
	private List<IntraAchievement> achievements;

	@Builder
	public IntraProfileResDto(String intraId, URL imageUrl, List<IntraAchievement> achievements) {
		this.intraId = intraId;
		this.imageUrl = imageUrl;
		this.achievements = achievements;
	}

	public static IntraProfileResDto toDto(IntraProfile intraProfile) {
		return IntraProfileResDto.builder()
			.intraId(intraProfile.getIntraId())
			.imageUrl(intraProfile.getImageUrl())
			.achievements(intraProfile.getAchievements())
			.build();
	}
}
