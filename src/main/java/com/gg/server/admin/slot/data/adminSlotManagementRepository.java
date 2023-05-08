package com.gg.server.admin.slot.data;

import com.gg.server.domain.slotmanagement.SlotManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface adminSlotManagementRepository extends JpaRepository<SlotManagement, Long> {
    Optional<SlotManagement> findFirstByOrderByCreatedAtDesc();
}
