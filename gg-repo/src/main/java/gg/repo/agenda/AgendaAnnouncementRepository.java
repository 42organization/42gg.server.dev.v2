package gg.repo.agenda;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;

public interface AgendaAnnouncementRepository extends JpaRepository<AgendaAnnouncement, Long> {

	Optional<AgendaAnnouncement> findFirstByAgendaAndIsShowIsTrueOrderByIdDesc(Agenda agenda);

	default Optional<AgendaAnnouncement> findLatestByAgenda(Agenda agenda) {
		return findFirstByAgendaAndIsShowIsTrueOrderByIdDesc(agenda);
	}
}
