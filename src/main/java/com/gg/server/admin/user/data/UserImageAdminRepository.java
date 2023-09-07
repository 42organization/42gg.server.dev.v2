package com.gg.server.admin.user.data;

import com.gg.server.admin.user.dto.UserImageAdminDto;
import com.gg.server.domain.user.data.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gg.server.domain.user.data.UserImage;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserImageAdminRepository extends JpaRepository<UserImage, Long>{
    Optional<UserImage> findTopByUserAndIsDeletedOrderByIdDesc(User user, Boolean isDeleted);
  
    Page<UserImage> findAllByIsDeleted(Pageable pageable, Boolean isDeleted);

    @Query(value = "SELECT ui FROM UserImage ui WHERE ui.user.id = ?1 " +
            "AND ui.isDeleted = true ORDER BY ui.id DESC")
    Page<UserImage> findAllByUserAndIsDeletedOrderByIdDesc(Long userId, Pageable pageable);

    @Query(value = "SELECT ui FROM UserImage ui WHERE ui.id NOT IN (" +
            "SELECT MIN(ui.id) FROM UserImage ui GROUP BY ui.user.id" +
            ") ORDER BY ui.user.id DESC")
    Page<UserImage> findAllChanged(Pageable pageable);

    @Query(value = "SELECT ui FROM UserImage ui WHERE ui.id NOT IN (" +
            "SELECT MIN(ui.id) FROM UserImage ui GROUP BY ui.user.id" +
            ") AND ui.user.id = ?1 ORDER BY ui.user.id DESC")
    Page<UserImage> findAllByUserOrderByIdDesc(Long userId, Pageable pageable);
}
