package gg.utils.converter;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiValueMapConverter {

	public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto) {
		try {
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<>() {
			});
			params.setAll(map);
			return params;
		} catch (Exception e) {
			log.error("Url Parameter 변환중 오류가 발생했습니다. requestDto={}", dto, e);
			throw new IllegalStateException("Url Parameter 변환중 오류가 발생했습니다.");
		}
	}
}
