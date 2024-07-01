package gg.repo.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.AgendaAnnouncement;

public interface AgendaAnnouncementRepository extends JpaRepository<AgendaAnnouncement, Long> {
}
