package gg.admin.repo.agenda;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;

@Repository
public interface AgendaAnnouncementAdminRepository extends JpaRepository<AgendaAnnouncement, Long> {

	Page<AgendaAnnouncement> findAllByAgenda(Agenda agenda, Pageable pageable);
}
