package gg.admin.repo.penalty;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.PartyPenalty;

public interface PartyPenaltyAdminRepository extends JpaRepository<PartyPenalty, Long> {

}
