package gg.admin.repo.agenda;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gg.data.agenda.AgendaProfile;

@Repository
public interface AgendaProfileAdminRepository extends JpaRepository<AgendaProfile, Long> {
	Optional<AgendaProfile> findByUserId(Long userId);
}
