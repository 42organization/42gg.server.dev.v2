package gg.agenda.api.user.agendaprofile.service.intraprofile;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntraProfileResponse {

	IntraImage image;

	List<IntraAchievement> achievements;

	@Builder
	public IntraProfileResponse(IntraImage image, List<IntraAchievement> achievements) {
		this.image = image;
		this.achievements = achievements;
	}
}
