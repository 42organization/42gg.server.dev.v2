package gg.pingpong.repo.store;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.store.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
