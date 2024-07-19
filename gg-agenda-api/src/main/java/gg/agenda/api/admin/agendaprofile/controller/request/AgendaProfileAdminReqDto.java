package gg.agenda.api.admin.agendaprofile.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AgendaProfileAdminReqDto {
	public String userContent;
	public String userGithub;
	public String userLocation;
	public String userProfilePic;

	public AgendaProfileAdminReqDto(String userContent, String userGithub, String userLocation, String userProfilePic) {
		this.userContent = userContent;
		this.userGithub = userGithub;
		this.userLocation = userLocation;
		this.userProfilePic = userProfilePic;
	}
}
