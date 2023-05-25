package com.gg.server.admin.feedback.dto;

import com.gg.server.global.dto.PageRequestDto;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
public class FeedbackAdminPageRequestDto extends PageRequestDto {
    @NotNull(message = "plz. intraId")
    String intraId;

    public FeedbackAdminPageRequestDto(String intraId, Integer page, Integer size) {
        super(page, size);
        this.intraId = intraId;
    }
}
