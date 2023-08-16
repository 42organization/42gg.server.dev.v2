package com.gg.server.domain.megaphone.data;

import com.gg.server.domain.receipt.data.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MegaphoneRepository extends JpaRepository<Megaphone, Long> {
    List<Megaphone> findAllByUsedAt(LocalDate date);

    Megaphone findFirstByOrderByIdDesc();

    Optional<Megaphone> findByReceipt(Receipt receipt);
}
