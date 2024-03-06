package gg.party.api.admin.comment.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateAdminRequestDto {
	private Boolean isHidden;
}
