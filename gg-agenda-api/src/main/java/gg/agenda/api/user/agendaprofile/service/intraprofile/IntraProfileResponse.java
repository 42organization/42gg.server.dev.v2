package gg.agenda.api.user.agendaprofile.service.intraprofile;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntraProfileResponse {
	String login;

	IntraImage image;

	List<IntraAchievement> achievements;

	@Builder
	public IntraProfileResponse(String login, IntraImage image, List<IntraAchievement> achievements) {
		this.login = login;
		this.image = image;
		this.achievements = achievements;
	}
}
