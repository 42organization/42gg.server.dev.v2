package com.gg.server.admin.announcement.controller;

import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.announcement.service.AnnouncementAdminService;
import com.gg.server.global.dto.PageRequestDto;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("pingpong/admin")
@Validated
public class AnnouncementAdminController {
    private final AnnouncementAdminService announcementAdminService;

    @GetMapping("/announcement")
    public ResponseEntity<AnnouncementAdminListResponseDto> getAnnouncementList(
            @ModelAttribute @Valid PageRequestDto anReq) {

        Pageable pageable = PageRequest.of(anReq.getPage() - 1, anReq.getSize(), Sort.by("createdAt").descending());

        return ResponseEntity.ok()
                .body(announcementAdminService.findAllAnnouncement(pageable));
    }

    @PostMapping("/announcement")
    public ResponseEntity addaAnnouncement(@Valid @RequestBody AnnouncementAdminAddDto addDto){
        announcementAdminService.addAnnouncement(addDto);

        return new ResponseEntity(HttpStatus.CREATED);
    }


    @DeleteMapping("/announcement/{deleterIntraId}")
    public ResponseEntity announcementModify(@PathVariable String deleterIntraId) {
        announcementAdminService.modifyAnnouncementIsDel(deleterIntraId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
