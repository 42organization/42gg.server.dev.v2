package gg.admin.repo.agenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gg.data.agenda.Agenda;

@Repository
public interface AgendaAdminRepository extends JpaRepository<Agenda, Long> {
}
