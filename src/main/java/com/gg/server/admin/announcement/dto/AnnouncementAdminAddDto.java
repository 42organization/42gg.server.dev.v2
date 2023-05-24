package com.gg.server.admin.announcement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementAdminAddDto {
    @NotNull(message = "plz. content")
    private String content;
    @NotNull(message = "plz. creatorIntraId")
    private String creatorIntraId;
}
