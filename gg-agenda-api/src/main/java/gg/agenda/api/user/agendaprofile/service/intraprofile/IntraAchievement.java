package gg.agenda.api.user.agendaprofile.service.intraprofile;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntraAchievement {

	private static final String IMAGE_URL = "https://cdn.intra.42.fr";

	private Long id;

	private String name;

	private String description;

	private String tier;

	private String kind;

	private boolean visible;

	private String image;

	@JsonProperty("nbr_of_success")
	private String nbrOfSuccess;

	@JsonProperty("users_url")
	private URL usersUrl;

	@Builder
	public IntraAchievement(Long id, String name, String description, String tier, String kind, boolean visible,
		String image, String nbrOfSuccess, URL usersUrl) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.tier = tier;
		this.kind = kind;
		this.visible = visible;
		this.image = image;
		this.nbrOfSuccess = nbrOfSuccess;
		this.usersUrl = usersUrl;
	}
}
