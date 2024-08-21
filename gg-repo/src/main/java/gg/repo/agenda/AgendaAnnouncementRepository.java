package gg.repo.agenda;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;

public interface AgendaAnnouncementRepository extends JpaRepository<AgendaAnnouncement, Long> {

	Optional<AgendaAnnouncement> findFirstByAgendaAndIsShowIsTrueOrderByIdDesc(Agenda agenda);

	Page<AgendaAnnouncement> findAllByAgendaAndIsShowIsTrueOrderByIdDesc(Pageable pageable, Agenda agenda);

	default Optional<AgendaAnnouncement> findLatestByAgenda(Agenda agenda) {
		return findFirstByAgendaAndIsShowIsTrueOrderByIdDesc(agenda);
	}

	default Page<AgendaAnnouncement> findListByAgenda(Pageable pageable, Agenda agenda) {
		return findAllByAgendaAndIsShowIsTrueOrderByIdDesc(pageable, agenda);
	}
}
