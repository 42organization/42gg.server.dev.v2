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

    @Query(value = "SELECT * FROM user_image WHERE id NOT IN (" +
            "SELECT MIN(id) FROM user_image GROUP BY user_id" +
            ") ORDER BY id DESC", nativeQuery = true)
    Page<UserImage> findAllChanged(Pageable pageable);

    @Query(value = "SELECT * FROM user_image WHERE id NOT IN (" +
            "SELECT MIN(id) FROM user_image GROUP BY user_id" +
            ") AND user_id = ?1 ORDER BY id DESC", nativeQuery = true)
    Page<UserImage> findAllByUserOrderByIdDesc(Pageable pageable, User user);
}
