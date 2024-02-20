package gg.pingpong.repo.megaphone;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.store.Megaphone;
import com.gg.server.data.store.Receipt;
import com.gg.server.data.store.type.ItemStatus;

public interface MegaphoneRepository extends JpaRepository<Megaphone, Long> {
	List<Megaphone> findAllByUsedAtAndReceiptStatus(LocalDate date, ItemStatus itemStatus);

	Megaphone findFirstByOrderByIdDesc();

	Optional<Megaphone> findByReceipt(Receipt receipt);
}
