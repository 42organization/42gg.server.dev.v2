package gg.agenda.api.user.agendaprofile.service.intraprofile;

import java.net.URL;

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

	private String nbr_of_success;

	private URL users_url;

	@Builder
	public IntraAchievement(Long id, String name, String description, String tier, String kind, boolean visible,
		String image, String nbr_of_success, URL users_url) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.tier = tier;
		this.kind = kind;
		this.visible = visible;
		this.nbr_of_success = nbr_of_success;
		this.users_url = users_url;
		this.image = image;
	}
}
