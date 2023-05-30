package com.gg.server.admin.slotmanagement.data;

import com.gg.server.domain.slotmanagement.SlotManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface adminSlotManagementRepository extends JpaRepository<SlotManagement, Long> {
    List<SlotManagement> findAllByOrderByCreatedAtDesc();
}
