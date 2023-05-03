package com.gg.server.admin.season.repository;

import com.gg.server.domain.season.Season;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonAdminRepository extends JpaRepository<Season, Long> {

}
