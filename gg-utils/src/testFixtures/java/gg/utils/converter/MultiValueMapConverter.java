package gg.utils.converter;

import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiValueMapConverter {

	public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto) { // (2)
		try {
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<Map<String, String>>() {
			}); // (3)
			params.setAll(map); // (4)
			return params;
		} catch (Exception e) {
			log.error("Url Parameter 변환중 오류가 발생했습니다. requestDto={}", dto, e);
			throw new IllegalStateException("Url Parameter 변환중 오류가 발생했습니다.");
		}
	}
}
