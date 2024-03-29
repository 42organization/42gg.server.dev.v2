package gg.party.api.user.room.controller.response;

import java.time.LocalDateTime;

import gg.data.party.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CommentResDto {
	private Long commentId;
	private String nickname;
	private String intraId;
	private Boolean isExist;
	private String content;
	private Boolean isHidden;
	private LocalDateTime createDate;

	public CommentResDto(Comment comment, String intraId) {
		this.commentId = comment.getId();
		this.nickname = comment.getUserRoom().getNickname();
		this.intraId = intraId;
		this.isExist = comment.getUserRoom().getIsExist();
		this.content = comment.getContent();
		this.isHidden = comment.isHidden();
		this.createDate = comment.getCreatedAt();
	}

	public CommentResDto(Comment comment) {
		this.commentId = comment.getId();
		this.nickname = comment.getUserRoom().getNickname();
		this.intraId = null;
		this.isExist = comment.getUserRoom().getIsExist();
		this.content = comment.getContent();
		this.isHidden = comment.isHidden();
		this.createDate = comment.getCreatedAt();
	}
}
