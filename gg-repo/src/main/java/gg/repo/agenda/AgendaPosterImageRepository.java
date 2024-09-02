package gg.repo.agenda;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.AgendaPosterImage;

public interface AgendaPosterImageRepository extends JpaRepository<AgendaPosterImage, Long> {

	Optional<AgendaPosterImage> findByAgendaIdAndIsCurrentFalse(Long agendaId);

	Optional<AgendaPosterImage> findByAgendaIdAndIsCurrentTrue(Long agendaId);
}
