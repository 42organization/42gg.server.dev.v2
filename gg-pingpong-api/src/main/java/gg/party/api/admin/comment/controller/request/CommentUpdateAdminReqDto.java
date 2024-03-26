package gg.party.api.admin.comment.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUpdateAdminReqDto {
	private Boolean isHidden;

	public CommentUpdateAdminReqDto(Boolean isHidden) {
		this.isHidden = isHidden;
	}
}
