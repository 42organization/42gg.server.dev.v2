package gg.utils.file.handler;

import java.io.IOException;
import java.net.URL;

import org.springframework.web.multipart.MultipartFile;
public interface ImageHandler {

	URL uploadImageOrDefault(MultipartFile multipartFile, String filename) throws IOException;

	URL uploadImageFromUrlOrDefault(String imageUrl, String filename) throws IOException;
}
