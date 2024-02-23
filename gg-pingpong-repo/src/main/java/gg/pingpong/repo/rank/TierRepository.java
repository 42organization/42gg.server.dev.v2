package gg.pingpong.repo.rank;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gg.pingpong.data.game.Tier;

public interface TierRepository extends JpaRepository<Tier, Long> {

	@Query("SELECT t FROM Tier t WHERE t.id = (SELECT MIN(t.id) FROM Tier t)")
	Optional<Tier> findStartTier();

	Optional<Tier> findByName(String name);
}
