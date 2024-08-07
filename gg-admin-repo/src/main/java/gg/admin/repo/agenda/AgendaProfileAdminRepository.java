package gg.admin.repo.agenda;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gg.data.agenda.AgendaProfile;

@Repository
public interface AgendaProfileAdminRepository extends JpaRepository<AgendaProfile, Long> {
	@Query("SELECT a FROM AgendaProfile a WHERE a.intraId = :intraId")
	Optional<AgendaProfile> findByIntraId(String intraId);
}
