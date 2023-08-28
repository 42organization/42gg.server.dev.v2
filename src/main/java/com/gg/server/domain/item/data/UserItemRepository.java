package com.gg.server.domain.item.data;

import com.gg.server.domain.receipt.data.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserItemRepository extends JpaRepository<Receipt, Long> {

    @Query("select r from Receipt r where r.ownerIntraId = :intraId and (r.status = 'BEFORE' or r.status = 'USING') order by r.createdAt desc")
    Page<Receipt> findByOwnerIntraId(@Param("intraId") String intraId, Pageable pageable);
}
