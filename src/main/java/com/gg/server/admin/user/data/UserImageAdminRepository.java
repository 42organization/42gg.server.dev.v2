package com.gg.server.admin.user.data;

import com.gg.server.domain.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gg.server.domain.user.data.UserImage;

import java.util.Optional;

public interface UserImageAdminRepository extends JpaRepository<UserImage, Long>{
    Optional<UserImage> findTopByUserAndIsDeletedOrderByIdDesc(User user, Boolean isDeleted);
}
