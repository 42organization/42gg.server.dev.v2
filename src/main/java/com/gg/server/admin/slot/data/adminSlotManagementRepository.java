package com.gg.server.admin.slot.data;

import com.gg.server.domain.slotmanagement.SlotManagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface adminSlotManagementRepository extends JpaRepository<SlotManagement, Long> {
    SlotManagement findFirstByOrderByCreatedAtDesc();
}
