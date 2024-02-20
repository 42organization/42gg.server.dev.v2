package gg.pingpong.api.global.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class JpegMultipartFile implements MultipartFile {

	private final byte[] bytes;
	String name;
	String originalFilename;
	String contentType;
	boolean isEmpty;
	long size;

	public JpegMultipartFile(byte[] bytes, String name) {
		this.bytes = bytes;
		this.name = name;
		this.originalFilename = name + ".jpeg";
		this.contentType = "image/jpeg";
		this.size = bytes.length;
		this.isEmpty = false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		return isEmpty;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return bytes;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public void transferTo(File dest) throws IllegalStateException {
	}
}
