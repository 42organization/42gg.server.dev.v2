package gg.repo.party;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.PartyPenalty;

public interface PartyPenaltyRepository extends JpaRepository<PartyPenalty, Long> {
	public PartyPenalty findByUserId(Long id);
}
