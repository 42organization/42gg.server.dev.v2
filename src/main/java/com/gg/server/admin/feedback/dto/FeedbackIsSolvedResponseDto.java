package com.gg.server.admin.feedback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedbackIsSolvedResponseDto {
    private Boolean isSolved;

    public FeedbackIsSolvedResponseDto(Boolean val){
        this.isSolved = val;
    }
}
