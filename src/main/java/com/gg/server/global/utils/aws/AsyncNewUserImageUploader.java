package com.gg.server.global.utils.aws;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.global.utils.UserImageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;

@Component
public class AsyncNewUserImageUploader {
    private final UserImageHandler userImageHandler;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${info.image.defaultUrl}")
    private String defaultImageUrl;

    public AsyncNewUserImageUploader(UserImageHandler userImageHandler, UserRepository userRepository) {
        this.userImageHandler = userImageHandler;
        this.userRepository = userRepository;
    }

    @Async("asyncExecutor")
    public void upload(String intraId, String imageUrl) {
        String s3ImageUrl = userImageHandler.uploadAndGetS3ImageUri(intraId, imageUrl);
        if (defaultImageUrl.equals(s3ImageUrl)) {
            return ;
        }
        userRepository.findByIntraId(intraId).ifPresent(user -> {
            if (s3ImageUrl == null) {
                user.imageUpdate(defaultImageUrl);
            } else {
                user.imageUpdate(s3ImageUrl);
            }
            userRepository.save(user);
        });
    }

    @Transactional
    public void update(String intraId, MultipartFile multipartFile) throws IOException {
        User user =  userRepository.getUserByIntraId(intraId);
        String s3ImageUrl = userImageHandler.updateAndGetS3ImageUri(multipartFile, user);
        if (s3ImageUrl == null) {
            user.imageUpdate(defaultImageUrl);
        } else {
            user.imageUpdate(s3ImageUrl);
        }
    }
}
