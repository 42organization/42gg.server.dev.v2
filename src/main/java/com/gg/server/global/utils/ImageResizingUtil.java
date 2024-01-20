package com.gg.server.global.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageResizingUtil {
	public static byte[] resizeImageBytes(byte[] downloadedImageBytes, double ratio) throws IOException {
		InputStream inputStream = new ByteArrayInputStream(downloadedImageBytes);
		BufferedImage image = resize(ImageIO.read(inputStream), ratio);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpeg", baos);
		return baos.toByteArray();
	}

	private static BufferedImage resize(BufferedImage img, double ratio) {
		int newWidth = (int)(img.getWidth() * ratio);
		int newHeight = (int)(img.getHeight() * ratio);
		Image imageToResize = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		resizedImage.getGraphics().drawImage(imageToResize, 0, 0, newWidth, newHeight, null);
		return resizedImage;
	}
}
