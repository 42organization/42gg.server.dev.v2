package com.gg.server.domain.announcement.controller;

import com.gg.server.domain.announcement.dto.AnnouncementResponseDto;
import com.gg.server.domain.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pingpong/announcement")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @GetMapping
    public AnnouncementResponseDto findLastAnnounceContent() {
        return new AnnouncementResponseDto(announcementService.findLastAnnouncement().getContent());
    }
}
