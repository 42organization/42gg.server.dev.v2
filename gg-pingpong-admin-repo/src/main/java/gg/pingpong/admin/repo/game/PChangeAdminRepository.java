package gg.pingpong.admin.repo.game;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.game.PChange;

public interface PChangeAdminRepository extends JpaRepository<PChange, Long>, PChangeAdminRepositoryCustom {
}
