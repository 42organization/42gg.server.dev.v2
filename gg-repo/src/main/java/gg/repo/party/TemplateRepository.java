package gg.repo.party;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.GameTemplate;

public interface TemplateRepository extends JpaRepository<GameTemplate, Long> {
}
