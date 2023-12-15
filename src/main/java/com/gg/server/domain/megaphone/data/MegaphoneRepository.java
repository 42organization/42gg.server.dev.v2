package com.gg.server.domain.megaphone.data;

import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.type.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MegaphoneRepository extends JpaRepository<Megaphone, Long> {
    List<Megaphone> findAllByUsedAtAndReceiptStatus(LocalDate date, ItemStatus itemStatus);

    Megaphone findFirstByOrderByIdDesc();

    Optional<Megaphone> findByReceipt(Receipt receipt);
}
