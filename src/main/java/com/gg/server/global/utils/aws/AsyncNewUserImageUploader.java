package com.gg.server.global.utils.aws;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserImage;
import com.gg.server.domain.user.data.UserImageRepository;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.global.utils.UserImageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class AsyncNewUserImageUploader {
    private final UserImageHandler userImageHandler;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${info.image.defaultUrl}")
    private String defaultImageUrl;
    private final UserImageRepository userImageRepository;

    public AsyncNewUserImageUploader(UserImageHandler userImageHandler, UserRepository userRepository,
                                     UserImageRepository userImageRepository) {
        this.userImageHandler = userImageHandler;
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
    }

    @Async("asyncExecutor")
    public void upload(String intraId, String imageUrl) {
        String s3ImageUrl = userImageHandler.uploadAndGetS3ImageUri(intraId, imageUrl);
        if (defaultImageUrl.equals(s3ImageUrl)) {
            return ;
        }
        userRepository.findByIntraId(intraId).ifPresent(user -> {
            UserImage userImage = new UserImage(user, (s3ImageUrl != null) ? s3ImageUrl : defaultImageUrl,
                    LocalDateTime.now(), null, true);
            userImageRepository.save(userImage);
            user.updateImageUri(userImage.getImageUri());
        });
    }

    @Transactional
    public void update(String intraId, MultipartFile multipartFile) throws IOException {
        User user = userRepository.findByIntraId(intraId).get();
        String s3ImageUrl = userImageHandler.updateAndGetS3ImageUri(multipartFile, user);
        s3ImageUrl = s3ImageUrl == null ? defaultImageUrl : s3ImageUrl;
        UserImage userImage = new UserImage(user, s3ImageUrl, LocalDateTime.now(), null, true);
        userImageRepository.saveAndFlush(userImage);
        user.updateImageUri(s3ImageUrl);
    }
}
