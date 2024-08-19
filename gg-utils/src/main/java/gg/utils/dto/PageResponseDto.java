package gg.utils.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponseDto<T> {

	private Long totalSize;

	private List<T> content;

	@Builder
	public PageResponseDto(Long totalSize, List<T> content) {
		this.totalSize = totalSize;
		this.content = content;
	}

	public static <T> PageResponseDto<T> of(Long totalSize, List<T> content) {
		return PageResponseDto.<T>builder()
			.totalSize(totalSize)
			.content(content)
			.build();
	}
}
