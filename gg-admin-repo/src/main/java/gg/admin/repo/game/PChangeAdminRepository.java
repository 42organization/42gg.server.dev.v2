package gg.admin.repo.game;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.pingpong.game.PChange;

public interface PChangeAdminRepository extends JpaRepository<PChange, Long>, PChangeAdminRepositoryCustom {
}
