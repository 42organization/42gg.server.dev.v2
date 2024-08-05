package gg.pingpong.api.global.utils.aws;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gg.data.user.User;
import gg.data.user.UserImage;
import gg.repo.user.UserImageRepository;
import gg.repo.user.UserRepository;
import gg.utils.file.handler.ImageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncNewUserImageUploader {

	private final ImageHandler imageHandler;

	private final UserRepository userRepository;

	private final UserImageRepository userImageRepository;

	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;

	@Async("asyncExecutor")
	@Transactional
	public void upload(String intraId, String imageUrl) throws IOException {
		URL s3ImageUrl = imageHandler.uploadImageFromUrlOrDefault(imageUrl, intraId);
		if (defaultImageUrl.equals(s3ImageUrl.toString())) {
			return;
		}
		userRepository.findByIntraId(intraId).ifPresent(user -> {
			UserImage userImage = new UserImage(user, (s3ImageUrl.toString() != null) ? s3ImageUrl.toString() : defaultImageUrl,
				LocalDateTime.now(), null, true);
			userImageRepository.save(userImage);
			userRepository.updateUserImage(user.getId(), userImage.getImageUri());
		});
	}

	@Transactional
	public void update(String intraId, MultipartFile multipartFile) throws IOException {
		User user = userRepository.findByIntraId(intraId).get();
		String s3ImageUrl = imageHandler.uploadImageOrDefault(multipartFile, user.getIntraId()).toString();
		UserImage userImage = new UserImage(user, s3ImageUrl, LocalDateTime.now(), null, true);
		userImageRepository.saveAndFlush(userImage);
		user.updateImageUri(s3ImageUrl);
	}
}
