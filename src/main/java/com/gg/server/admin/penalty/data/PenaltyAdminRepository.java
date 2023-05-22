package com.gg.server.admin.penalty.data;

import com.gg.server.domain.penalty.data.Penalty;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PenaltyAdminRepository extends JpaRepository<Penalty, Long>{
    @EntityGraph(attributePaths = {"user"})
    Page<Penalty> findAll(Pageable pageable);

    @Query("select p from Penalty p join fetch p.user where " +
    "p.user.intraId like \'%:intraId%\' order by p.startTime desc")
    Page<Penalty> findAllByIntraId(Pageable pageable, @Param("intraId") String intraId);

    @Query("SELECT p FROM Penalty p WHERE p.startTime + p.penaltyTime * 60 * 60 > :targetTime")
    Page<Penalty> findAllCurrent(Pageable pageable, @Param("targetTime") LocalDateTime targetTime);

    @Query("select p from Penalty p join fetch p.user where " +
            "p.user.intraId like \'%:intraId%\' and p.startTime + p.penaltyTime * 60 * 60 > :targetTime "
            + "order by p.startTime desc")
    Page<Penalty> findAllCurrentByIntraId(Pageable pageable, @Param("targetTime") LocalDateTime targetTime,
                                          @Param("intraId") String intraId);

    @Query("select p from Penalty p where p.user.id = :userId and p.startTime > :startTime")
    List<Penalty> findAfterPenaltiesByUser(@Param("userId") Long userId,
                                                     @Param("startTime") LocalDateTime startTime);
}
