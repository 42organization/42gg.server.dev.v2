package gg.party.api.admin.room.controller.response;

import java.time.LocalDateTime;

import gg.data.party.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AdminCommentResDto {
	private Long commentId;
	private String nickname;
	private String intraId;
	private Boolean isExist;
	private String content;
	private Boolean isHidden;
	private LocalDateTime createDate;

	public AdminCommentResDto(Comment comment) {
		this.commentId = comment.getId();
		this.nickname = comment.getUserRoom().getNickname();
		this.intraId = comment.getUser().getIntraId();
		this.isExist = comment.getUserRoom().getIsExist();
		this.content = comment.getContent();
		this.isHidden = comment.isHidden();
		this.createDate = comment.getCreatedAt();
	}
}
