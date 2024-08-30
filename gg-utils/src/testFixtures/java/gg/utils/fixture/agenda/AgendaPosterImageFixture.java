package gg.utils.fixture.agenda;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaPosterImage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaPosterImageFixture {
	@PersistenceContext
	private final EntityManager em;

	public AgendaPosterImage createAgendaPosterImage(Agenda agenda, String posterUri) {
		AgendaPosterImage posterImage = new AgendaPosterImage(agenda.getId(), "posterUri");
		return em.merge(posterImage);
	}
}
