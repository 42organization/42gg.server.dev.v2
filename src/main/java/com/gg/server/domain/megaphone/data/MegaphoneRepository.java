package com.gg.server.domain.megaphone.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MegaphoneRepository extends JpaRepository<Megaphone, Long> {
    List<Megaphone> findAllByUsedAt(LocalDate date);
}
