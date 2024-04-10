package gg.repo.party;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.party.PartyPenalty;

public interface PartyPenaltyRepository extends JpaRepository<PartyPenalty, Long> {
	Optional<PartyPenalty> findTopByUserIdOrderByStartTimeDesc(@Param("userId") Long userId);

	List<PartyPenalty> findAllByUserId(Long userId);

	@Query(value = "SELECT pp FROM PartyPenalty pp "
		+ "JOIN FETCH pp.user ",
		countQuery = "SELECT count(pp) FROM PartyPenalty pp")
	Page<PartyPenalty> findUserFetchJoin(Pageable pageable);
}
