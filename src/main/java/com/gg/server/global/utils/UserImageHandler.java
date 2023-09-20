package com.gg.server.global.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserImage;
import com.gg.server.domain.user.data.UserImageRepository;
import com.gg.server.domain.user.exception.UserImageNullException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class UserImageHandler {
    private final AmazonS3 amazonS3;
    private final FileDownloader fileDownloader;

    public UserImageHandler(AmazonS3 amazonS3, FileDownloader fileDownloader,
                            UserImageRepository userImageRepository) {
        this.amazonS3 = amazonS3;
        this.fileDownloader = fileDownloader;
        this.userImageRepository = userImageRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.dir}")
    private String dir;

    @Value("${info.image.defaultUrl}")
    private String defaultImageUrl;
    private final UserImageRepository userImageRepository;

    public String uploadAndGetS3ImageUri(String intraId, String imageUrl) {
        if (!isStringValid(intraId) || !isStringValid(imageUrl)) {
            return defaultImageUrl;
        }
        byte[] downloadedImageBytes = fileDownloader.downloadFromUrl(imageUrl);
        try {
            byte[] resizedImageBytes = ImageResizingUtil.resizeImageBytes(downloadedImageBytes, 0.5);
            MultipartFile multipartFile = new JpegMultipartFile(resizedImageBytes, intraId);
            return uploadToS3(multipartFile, multipartFile.getOriginalFilename());
        } catch (IOException e) {
            return defaultImageUrl;
        }
    }

    public String updateAndGetS3ImageUri(MultipartFile multipartFile, User user) throws IOException
    {
        String updateFileName = user.getIntraId() + "-" + UUID.randomUUID().toString() + ".jpeg";
        if (updateFileName.equals("small_default.jpeg"))
            return defaultImageUrl;
        else {
            String s3ImageUrl = uploadToS3(multipartFile, updateFileName);;
            return s3ImageUrl;
        }
    }

    private Boolean isStringValid(String intraId) {
        return intraId != null && intraId.length() != 0;
    }

    public String uploadToS3(MultipartFile multipartFile, String fileName) throws IOException{
        String s3FileName = dir + fileName;
        InputStream inputStream = multipartFile.getInputStream();
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getSize());
        amazonS3.putObject(new PutObjectRequest(bucketName, s3FileName, inputStream, objMeta).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }
}
