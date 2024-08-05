package gg.utils.file.handler;

import java.io.IOException;
import java.net.URL;

import org.springframework.web.multipart.MultipartFile;
public interface ImageHandler {

	URL uploadImage(MultipartFile multipartFile, String filename) throws IOException;

	URL uploadImageFromUrl(String imageUrl, String filename) throws IOException;
}
