package gg.utils.file.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;

	@Override
	public URL uploadImage(MultipartFile multipartFile, String filename) throws IOException {
		String originalFilename = multipartFile.getOriginalFilename();
		String storeFileName = createStoredFileName(originalFilename, filename);
		return uploadToS3(multipartFile, storeFileName);
	}

	@Override
	public URL uploadImageFromUrl(URL imageUrl, String filename) throws IOException {
		byte[] downloadedImageBytes = fileDownloader.downloadFromUrl(imageUrl.toString());
		byte[] resizedImageBytes = ImageResizingUtil.resizeImageBytes(downloadedImageBytes, 0.5);
		MultipartFile multipartFile = new JpegMultipartFile(resizedImageBytes, filename);
		return uploadToS3(multipartFile, multipartFile.getOriginalFilename());
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

	URL uploadToS3(MultipartFile multipartFile, String fileName) throws IOException {
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
