package gg.pingpong.api.user.manage.controller.request;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import gg.data.manage.type.FeedbackType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedbackRequestDto {
	@NotNull(message = "plz. not null FeedbackType")
	private FeedbackType category;
	@NotNull(message = "plz.  not null content")
	@Length(max = 600, message = "plz. maxSizeMessage 600")
	private String content;

	@Builder
	public FeedbackRequestDto(FeedbackType category, String content) {
		this.category = category;
		this.content = content;
	}
}
