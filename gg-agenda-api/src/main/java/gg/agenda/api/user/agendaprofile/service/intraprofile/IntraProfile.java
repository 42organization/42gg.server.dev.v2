package gg.agenda.api.user.agendaprofile.service.intraprofile;

import java.net.URL;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntraProfile {
	private String intraId;

	private URL imageUrl;

	private List<IntraAchievement> achievements;

	@Builder
	public IntraProfile(String intraId, URL imageUrl, List<IntraAchievement> achievements) {
		this.intraId = intraId;
		this.imageUrl = imageUrl;
		this.achievements = achievements;
	}
}
