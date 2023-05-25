package com.gg.server.admin.feedback.dto;

import com.gg.server.domain.feedback.data.Feedback;
import com.gg.server.domain.feedback.type.FeedbackType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Getter
@NoArgsConstructor
public class FeedbackAdminResponseDto {
    private Long id;
    private String intraId;
    private Date createdAt;
    private FeedbackType category;
    private String content;
    private Boolean isSolved;

    public FeedbackAdminResponseDto(Feedback feedback){
        this.id = feedback.getId();
        this.intraId = feedback.getUser().getIntraId();
        this.createdAt = Date.valueOf(feedback.getCreatedAt().toLocalDate());
        this.category = feedback.getCategory();
        this.content = feedback.getContent();
        this.isSolved = feedback.getIsSolved();
    }
}
