package com.gg.server.admin.receipt.data;

import com.gg.server.domain.receipt.data.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceiptAdminRepositorySearch {
    @Query(value = "select re from Receipt re where re.purchaserIntraId = :intraId or re.ownerIntraId = :intraId order by re.createdAt desc")
    Page<Receipt> findReceiptByIntraId(@Param("intraId") String intraId, Pageable pageable);
}
