package gg.pingpong.api.global.utils.aws;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import gg.data.pingpong.store.Item;

@Component
public class ItemImageHandler {
	private final AmazonS3 amazonS3;

	public ItemImageHandler(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.s3.dir}")
	private String dir;

	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;

	public String updateAndGetS3ImageUri(MultipartFile multipartFile, Item item) throws IOException {
		String itemFileName = item.getName() + "-" + UUID.randomUUID() + ".jpeg";
		if (itemFileName.equals("small_default.jpeg")) {
			return defaultImageUrl;
		} else {
			String s3ImageUrl = uploadToS3(multipartFile, itemFileName);
			return s3ImageUrl;
		}
	}

	public String uploadToS3(MultipartFile multipartFile, String fileName) throws IOException {
		String s3FileName = dir + fileName;
		InputStream inputStream = multipartFile.getInputStream();
		ObjectMetadata objMeta = new ObjectMetadata();
		objMeta.setContentLength(multipartFile.getSize());
		amazonS3.putObject(new PutObjectRequest(bucketName, s3FileName, inputStream, objMeta).withCannedAcl(
			CannedAccessControlList.PublicRead));
		return amazonS3.getUrl(bucketName, s3FileName).toString();
	}
}
