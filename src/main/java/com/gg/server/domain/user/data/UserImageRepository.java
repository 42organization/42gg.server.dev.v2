package com.gg.server.domain.user.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
	Optional<UserImage> findTopByUserAndIsCurrentIsTrueOrderByIdDesc(User user);
}
