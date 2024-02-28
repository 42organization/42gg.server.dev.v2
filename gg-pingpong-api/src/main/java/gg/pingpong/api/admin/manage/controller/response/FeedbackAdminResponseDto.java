package gg.pingpong.api.admin.manage.controller.response;

import java.time.LocalDateTime;

import gg.pingpong.data.manage.Feedback;
import gg.pingpong.data.manage.type.FeedbackType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedbackAdminResponseDto {
	private Long id;
	private String intraId;
	private LocalDateTime createdAt;
	private FeedbackType category;
	private String content;
	private Boolean isSolved;

	public FeedbackAdminResponseDto(Feedback feedback) {
		this.id = feedback.getId();
		this.intraId = feedback.getUser().getIntraId();
		this.createdAt = feedback.getCreatedAt();
		this.category = feedback.getCategory();
		this.content = feedback.getContent();
		this.isSolved = feedback.getIsSolved();
	}
}
