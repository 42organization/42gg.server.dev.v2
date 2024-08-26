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

	private URL imageUrl;

	private List<IntraAchievement> achievements;

	@Builder
	public IntraProfile(URL imageUrl, List<IntraAchievement> achievements) {
		this.imageUrl = imageUrl;
		this.achievements = achievements;
	}
}
