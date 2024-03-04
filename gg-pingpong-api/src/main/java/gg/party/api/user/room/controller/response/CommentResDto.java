package gg.party.api.user.room.controller.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentResDto {
	private Long commentId;
	private String nickname;
	private String content;
	private Boolean isHidden;
	private LocalDateTime createDate;

	public CommentResDto(Long id, String nickname, String content, boolean hidden, LocalDateTime createdAt) {
		this.commentId = id;
		this.nickname = nickname;
		this.content = content;
		this.isHidden = hidden;
		this.createDate = createdAt;
	}
}
