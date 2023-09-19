package com.gg.server.domain.feedback.dto;

import com.gg.server.domain.feedback.type.FeedbackType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class FeedbackRequestDto {
    @NotNull(message = "plz. not null FeedbackType")
    private FeedbackType category;
    @NotNull(message = "plz.  not null content")
    @Length(max = 600, message = "plz. maxSizeMessage 600")
    private String content;

    @Builder
    public FeedbackRequestDto(FeedbackType category, String content) {
        this.category = category;
        this.content = content;
    }
}
