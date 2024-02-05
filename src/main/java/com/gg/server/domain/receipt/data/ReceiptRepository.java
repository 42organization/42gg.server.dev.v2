package com.gg.server.domain.receipt.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.store.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
