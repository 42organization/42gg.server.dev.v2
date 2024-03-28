package gg.repo.store;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.pingpong.store.Megaphone;
import gg.data.pingpong.store.Receipt;
import gg.data.pingpong.store.type.ItemStatus;

public interface MegaphoneRepository extends JpaRepository<Megaphone, Long> {
	List<Megaphone> findAllByUsedAtAndReceiptStatus(LocalDate date, ItemStatus itemStatus);

	Megaphone findFirstByOrderByIdDesc();

	Optional<Megaphone> findByReceipt(Receipt receipt);
}
