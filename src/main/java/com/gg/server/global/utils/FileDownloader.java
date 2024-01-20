package com.gg.server.global.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FileDownloader {
	private RestTemplate restTemplate;

	public FileDownloader() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(5000); // 타임아웃 설정 5초
		factory.setReadTimeout(5000); // 타임아웃 설정 5초

		//Apache HttpComponents : 각 호스트(IP와 Port의 조합)당 커넥션 풀에 생성가능한 커넥션 수
		HttpClient httpClient = HttpClientBuilder.create()
			.setMaxConnTotal(50)//최대 커넥션 수
			.setMaxConnPerRoute(20).build();
		factory.setHttpClient(httpClient);

		// 2. RestTemplate 객체를 생성합니다.
		this.restTemplate = new RestTemplate(factory);
	}

	public byte[] downloadFromUrl(String imageUrl) {
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(imageUrl).build(false);
		return restTemplate.getForObject(uri.toString(), byte[].class);
	}
}
