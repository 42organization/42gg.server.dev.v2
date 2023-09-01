package com.gg.server.global.utils.aws;

import com.gg.server.domain.item.data.Item;
import com.gg.server.global.utils.ItemImageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class AsyncNewItemImageUploader {
    private final ItemImageHandler itemImageHandler;

    @Value("${info.image.defaultUrl}")
    private String defaultImageUrl;

    public AsyncNewItemImageUploader(ItemImageHandler itemImageHandler) {
        this.itemImageHandler = itemImageHandler;
    }

    @Transactional
    public void upload(Item item,
                       MultipartFile multipartFile) throws IOException {
        String s3ImageUrl = itemImageHandler.updateAndGetS3ImageUri(multipartFile, item);
        if (s3ImageUrl == null) {
            item.imageUpdate(defaultImageUrl);
        } else {
            item.imageUpdate(s3ImageUrl);
        }
    }
}
