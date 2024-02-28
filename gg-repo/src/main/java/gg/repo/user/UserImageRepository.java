package gg.repo.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.user.User;
import gg.data.user.UserImage;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
	Optional<UserImage> findTopByUserAndIsCurrentIsTrueOrderByIdDesc(User user);
}
