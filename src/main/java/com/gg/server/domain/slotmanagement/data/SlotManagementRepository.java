package com.gg.server.domain.slotmanagement.data;

import com.gg.server.domain.slotmanagement.SlotManagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotManagementRepository
    extends JpaRepository<SlotManagement, Long> {
    SlotManagement findFirstByOrderByCreatedAtDesc();
}
