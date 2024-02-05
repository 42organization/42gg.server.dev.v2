package com.gg.server.domain.pchange.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gg.server.data.game.PChange;
import com.gg.server.data.game.type.Mode;

public interface PChangeRepository extends JpaRepository<PChange, Long>, PChangeRepositoryCustom {

	@Query(value = "SELECT pc FROM PChange pc join fetch pc.user "
		+ "WHERE pc.user.intraId LIKE %:intraId% "
		+ "order by pc.user.intraId asc, pc.id desc")
	List<PChange> findPChangesByUser_IntraId(@Param("intraId") String intraId);

	@Query(value = "SELECT pc FROM PChange pc join fetch pc.user WHERE pc.user.id =:userId order by pc.id desc")
	List<PChange> findAllByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT pc FROM PChange pc join fetch pc.user join fetch pc.game "
		+ "WHERE pc.user.id = :userId and pc.game.mode in :modes "
		+ "order by pc.id desc")
	List<PChange> findAllByUserIdGameModeIn(@Param("userId") Long userId, @Param("modes") List<Mode> modes);

	Optional<PChange> findByUserIdAndGameId(Long userId, Long gameId);

	Optional<PChange> findPChangeByUserIdAndGameId(Long userId, Long gameId);

	List<PChange> findPChangesByGameId(Long gameId);
}
