package gg.pingpong.api.global.utils.aws;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gg.data.store.Item;
import gg.pingpong.api.global.utils.ItemImageHandler;

@Component
public class AsyncNewItemImageUploader {

	private final ItemImageHandler itemImageHandler;

	@Value("${info.image.itemNotFoundUrl}")
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
