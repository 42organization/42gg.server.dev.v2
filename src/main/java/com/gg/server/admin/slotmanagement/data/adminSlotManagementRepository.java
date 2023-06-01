package com.gg.server.admin.slotmanagement.data;

import com.gg.server.domain.slotmanagement.SlotManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface adminSlotManagementRepository extends JpaRepository<SlotManagement, Long> {
    List<SlotManagement> findAllByOrderByCreatedAtDesc();

    Optional<SlotManagement> findFirstByOrderByIdDesc();

}
