package gg.pingpong.repo.store;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.store.Megaphone;
import gg.pingpong.data.store.Receipt;
import gg.pingpong.data.store.type.ItemStatus;

public interface MegaphoneRepository extends JpaRepository<Megaphone, Long> {
	List<Megaphone> findAllByUsedAtAndReceiptStatus(LocalDate date, ItemStatus itemStatus);

	Megaphone findFirstByOrderByIdDesc();

	Optional<Megaphone> findByReceipt(Receipt receipt);
}
