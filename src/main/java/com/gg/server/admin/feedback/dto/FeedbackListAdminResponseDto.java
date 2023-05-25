package com.gg.server.admin.feedback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class FeedbackListAdminResponseDto {
    private List<FeedbackAdminResponseDto> feedbackList;
    private int totalPage;
    private int currentPage;

    public FeedbackListAdminResponseDto(List<FeedbackAdminResponseDto> newDtos, int totalPage){
        this.feedbackList = newDtos;
        this.totalPage= totalPage;
    }
}
