package com.gg.server.admin.receipt.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.domain.receipt.data.Receipt;

public interface ReceiptAdminRepository extends JpaRepository<Receipt, Long>, ReceiptAdminRepositorySearch {
	Page<Receipt> findAll(Pageable pageable);
}
