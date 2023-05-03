package com.gg.server.admin.season.data;

import com.gg.server.domain.season.data.Season;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonAdminRepository extends JpaRepository<Season, Long> {

}
