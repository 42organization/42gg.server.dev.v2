package com.gg.server.admin.announcement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AnnouncementAdminListResponseDto {
    private List<AnnouncementAdminResponseDto> announcementList;
    private int totalPage;
}
