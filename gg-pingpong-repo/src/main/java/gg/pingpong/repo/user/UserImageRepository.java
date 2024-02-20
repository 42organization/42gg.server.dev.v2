package gg.pingpong.repo.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.user.User;
import com.gg.server.data.user.UserImage;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
	Optional<UserImage> findTopByUserAndIsCurrentIsTrueOrderByIdDesc(User user);
}
