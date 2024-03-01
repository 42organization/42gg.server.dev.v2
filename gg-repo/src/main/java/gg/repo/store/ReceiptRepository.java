package gg.repo.store;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.store.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
