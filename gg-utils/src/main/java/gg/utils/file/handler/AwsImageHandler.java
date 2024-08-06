package gg.utils.file.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import gg.utils.file.FileDownloader;
import gg.utils.file.ImageResizingUtil;
import gg.utils.file.JpegMultipartFile;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AwsImageHandler implements ImageHandler {

	private final AmazonS3 amazonS3;

	private final FileDownloader fileDownloader;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.s3.dir}")
	private String dir;

	@Override
	public URL uploadImageOrDefault(MultipartFile multipartFile, String filename, String defaultUrl) throws IOException {
		if (filename.isBlank() || isDefaultImage(multipartFile)) {
			return new URL(defaultUrl);
		}
		String originalFilename = multipartFile.getOriginalFilename();
		String storeFileName = createStoredFileName(originalFilename, filename);
		URL storedUrl = uploadToS3(multipartFile, storeFileName);
		if (Objects.isNull(storedUrl)) {
			return new URL(defaultUrl);
		}
		return storedUrl;
	}

	private static boolean isDefaultImage(MultipartFile multipartFile) {
		if (Objects.isNull(multipartFile)) {
			return true;
		}
		if (Objects.isNull(multipartFile.getOriginalFilename())) {
			return true;
		}
		return multipartFile.getOriginalFilename().equals("small_default.jpeg");
	}

	@Override
	public URL uploadImageFromUrlOrDefault(String imageUrl, String filename, String defaultUrl) throws IOException {
		if (filename.isBlank() || ResourcePatternUtils.isUrl(imageUrl)) {
			return new URL(defaultUrl);
		}
		byte[] downloadedImageBytes = fileDownloader.downloadFromUrl(imageUrl);
		byte[] resizedImageBytes = ImageResizingUtil.resizeImageBytes(downloadedImageBytes, 0.5);
		MultipartFile multipartFile = new JpegMultipartFile(resizedImageBytes, filename);
		URL storedUrl = uploadToS3(multipartFile, multipartFile.getOriginalFilename());
		if (Objects.isNull(storedUrl)) {
			return new URL(defaultUrl);
		}
		return storedUrl;
	}

	private String createStoredFileName(String originalFilename, String filename) {
		String ext = extractExtensionOrDefault(originalFilename, "jpeg");
		return filename + "-" + UUID.randomUUID() + "." + ext;
	}

	private String extractExtensionOrDefault(String uploadFileName, String defaultExtension) {
		if (uploadFileName == null) {
			return defaultExtension;
		}

		int pos = uploadFileName.lastIndexOf(".");
		if (pos == -1) {
			return defaultExtension;
		}

		return uploadFileName.substring(pos + 1);
	}

	public URL uploadToS3(MultipartFile multipartFile, String fileName) throws IOException {
		String s3FileName = this.dir + fileName;
		InputStream inputStream = multipartFile.getInputStream();

		ObjectMetadata objMeta = new ObjectMetadata();
		objMeta.setContentLength(multipartFile.getSize());

		PutObjectRequest putObjectRequest = new PutObjectRequest(
			bucketName,
			s3FileName,
			inputStream,
			objMeta
		).withCannedAcl(CannedAccessControlList.PublicRead);
		amazonS3.putObject(putObjectRequest);
		return amazonS3.getUrl(bucketName, s3FileName);
	}
}
