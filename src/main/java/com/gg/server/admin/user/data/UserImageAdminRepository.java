package com.gg.server.admin.user.data;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserImage;

public interface UserImageAdminRepository extends JpaRepository<UserImage, Long> {
	Optional<UserImage> findTopByUserAndIsCurrentIsTrueOrderByCreatedAtDesc(User user);

	Optional<UserImage> findTopByUserAndDeletedAtIsNullOrderByCreatedAtDesc(User user);

	Page<UserImage> findAllByDeletedAtNotNullOrderByDeletedAtDesc(Pageable pageable);

	@Query(value = "SELECT ui FROM UserImage ui WHERE ui.user.id = :user_id "
		+ "AND ui.deletedAt != NULL ORDER BY ui.deletedAt DESC")
	Page<UserImage> findAllByUserAndDeletedAtNotNullOrderByDeletedAtDesc(@Param("user_id") Long userId,
		Pageable pageable);

	@Query(value = "SELECT ui FROM UserImage ui WHERE ui.id NOT IN ("
		+ "SELECT MIN(ui.id) FROM UserImage ui GROUP BY ui.user.id"
		+ ") ORDER BY ui.createdAt DESC")
	Page<UserImage> findAllChangedOrderByCreatedAtDesc(Pageable pageable);

	@Query(value = "SELECT ui FROM UserImage ui WHERE ui.id NOT IN ("
		+ "SELECT MIN(ui.id) FROM UserImage ui GROUP BY ui.user.id"
		+ ") AND ui.user.id = :user_id ORDER BY ui.createdAt DESC")
	Page<UserImage> findAllByUserOrderByCreatedAtDesc(@Param("user_id") Long userId, Pageable pageable);

	@Query(value = "SELECT ui FROM UserImage ui WHERE ui.isCurrent IS TRUE"
		+ " ORDER BY ui.createdAt DESC")
	Page<UserImage> findAllByIsCurrentTrueOrderByCreatedAtDesc(Pageable pageable);

	@Query(value = "SELECT ui FROM UserImage ui WHERE ui.isCurrent IS True"
		+ " AND ui.user.id = :user_id ORDER BY ui.createdAt DESC")
	Page<UserImage> findAllByUserAndIsCurrentTrueOrderByCreatedAtDesc(@Param("user_id") Long userId, Pageable pageable);
}
