package gg.repo.agenda;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.AgendaProfile;

public interface AgendaProfileRepository extends JpaRepository<AgendaProfile, Long> {
	Optional<AgendaProfile> findByUserId(Long userId);

	Optional<AgendaProfile> findByIntraId(String intraId);
}
