package gg.repo.tournarment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.tournament.Tournament;
import gg.data.tournament.type.TournamentStatus;
import gg.data.tournament.type.TournamentType;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
	List<Tournament> findAllByStatusIsNot(TournamentStatus status);

	Optional<Tournament> findByTitle(String title);

	Page<Tournament> findAllByTypeAndStatus(@NotNull TournamentType type, @NotNull TournamentStatus status,
		Pageable pageable);

	Page<Tournament> findAllByStatus(@NotNull TournamentStatus status, Pageable pageable);

	List<Tournament> findAllByStatus(@NotNull TournamentStatus status);

	Page<Tournament> findAllByType(@NotNull TournamentType type, Pageable pageable);

	@Query(value = "select t from Tournament t where (t.startTime between :startTime and :endTime) "
		+ "or (t.endTime between :startTime and :endTime) "
		+ "or (:startTime between t.startTime and t.endTime) "
		+ "or (:endTime between t.startTime and t.endTime)")
	List<Tournament> findAllBetween(@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime);

}
