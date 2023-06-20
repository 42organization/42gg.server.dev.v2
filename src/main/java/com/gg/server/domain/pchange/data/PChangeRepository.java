package com.gg.server.domain.pchange.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PChangeRepository extends JpaRepository<PChange, Long> , PChangeRepositoryCustom{

    @Query(value = "SELECT pc FROM PChange pc join fetch pc.user WHERE pc.user.intraId LIKE %:intraId% order by pc.user.intraId asc, pc.id desc")
    List<PChange> findPChangesByUser_IntraId(@Param("intraId") String intraId);

    @Query(value = "SELECT pc FROM PChange pc join fetch pc.user WHERE pc.user.id =:userId order by pc.id desc")
    List<PChange> findAllByUserId(@Param("userId") Long userId);

    Optional<PChange> findByUserIdAndGameId(Long userId, Long gameId);

    Optional<PChange> findPChangeByUserIdAndGameId(Long userId, Long gameId);
}
