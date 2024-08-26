package gg.agenda.api.user.agendaprofile.service.intraprofile;

import java.net.URL;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntraImage {

	private URL link;

	private IntraImageVersion versions;

	@Builder
	public IntraImage(URL link, IntraImageVersion versions) {
		this.link = link;
		this.versions = versions;
	}
}
