package gg.party.api.user.comment.controller.request;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateReqDto {
	@Size(min = 1, max = 100)
	private String content;

	public void saveContent(String content) {
		this.content = content;
	}
}
