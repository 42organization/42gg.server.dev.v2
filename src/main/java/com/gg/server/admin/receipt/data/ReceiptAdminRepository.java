package com.gg.server.admin.receipt.data;

import com.gg.server.domain.receipt.data.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptAdminRepository extends JpaRepository<Receipt, Long> {
    Page<Receipt> findAll(Pageable pageable);
}
