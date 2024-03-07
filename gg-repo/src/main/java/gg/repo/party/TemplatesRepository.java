package gg.repo.party;

import javax.xml.transform.Templates;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.GameTemplate;

public interface TemplatesRepository extends JpaRepository<GameTemplate, Long> {
}
