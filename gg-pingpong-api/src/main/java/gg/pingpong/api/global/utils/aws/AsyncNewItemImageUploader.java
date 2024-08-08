package gg.pingpong.api.global.utils.aws;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gg.data.pingpong.store.Item;
import gg.utils.file.handler.ImageHandler;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AsyncNewItemImageUploader {

	private final ImageHandler imageHandler;

	@Value("${info.image.itemNotFoundUrl}")
	private String defaultUrl;

	@Transactional
	public void upload(Item item, MultipartFile multipartFile) throws IOException {
		URL s3ImageUrl = imageHandler.uploadImageOrDefault(multipartFile, item.getName(), defaultUrl);
		item.imageUpdate(s3ImageUrl.toString());
	}
}
