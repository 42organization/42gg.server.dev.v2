package com.gg.server.admin.announcement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementAdminAddDto {
    @NotNull(message = "plz. content")
    private String content;
    @NotNull(message = "plz. creatorIntraId")
    private String creatorIntraId;
}
