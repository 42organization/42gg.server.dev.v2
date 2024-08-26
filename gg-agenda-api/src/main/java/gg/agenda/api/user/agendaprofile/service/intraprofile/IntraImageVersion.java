package gg.agenda.api.user.agendaprofile.service.intraprofile;

import java.net.URL;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntraImageVersion {

	private URL large;

	private URL medium;

	private URL small;

	private URL micro;

	@Builder
	public IntraImageVersion(URL large, URL medium, URL small, URL micro) {
		this.large = large;
		this.medium = medium;
		this.small = small;
		this.micro = micro;
	}
}
