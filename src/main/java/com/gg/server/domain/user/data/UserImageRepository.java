package com.gg.server.domain.user.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    Optional<UserImage> findTopByUserAndIsCurrentIsTrueOrderByIdDesc(User user);
}
