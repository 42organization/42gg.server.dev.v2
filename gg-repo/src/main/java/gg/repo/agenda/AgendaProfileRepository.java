package gg.repo.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.agenda.AgendaProfile;

public interface AgendaProfileRepository extends JpaRepository<AgendaProfile, Long> {
}
