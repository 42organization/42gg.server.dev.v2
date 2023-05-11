package com.gg.server.admin.announcement.controller;

import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.announcement.service.AnnouncementAdminService;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.AdminException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("pingpong/admin")
@Slf4j
public class AnnouncementAdminController {
    private final AnnouncementAdminService announcementAdminService;

    @GetMapping("/announcement")
    public AnnouncementAdminListResponseDto getAnnouncementList(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size) {
        if (page < 1 || size < 1)
            throw new AdminException("요청한 페이징 불가", ErrorCode.BAD_REQUEST);
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("createdAt").descending());
        return announcementAdminService.findAllAnnouncement(pageable);
    }
}
