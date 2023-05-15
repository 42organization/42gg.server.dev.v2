package com.gg.server.admin.announcement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementAdminUpdateDto {
    @NotNull(message = "plz. creatorIntraId")
    private String deleterIntraId;
}
