package gg.pingpong.admin.repo.receipt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gg.server.data.store.Receipt;

public interface ReceiptAdminRepositorySearch {
	@Query(value = "select re from Receipt re where re.purchaserIntraId = :intraId or re.ownerIntraId = :intraId")
	Page<Receipt> findReceiptByIntraId(@Param("intraId") String intraId, Pageable pageable);
}
