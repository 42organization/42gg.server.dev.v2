package com.gg.server.admin.announcement.service;

import com.gg.server.admin.announcement.data.AnnouncementAdminRepository;
import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminResponseDto;
import com.gg.server.domain.announcement.Announcement;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.AdminException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AnnouncementAdminService {
    private final AnnouncementAdminRepository announcementAdminRepository;

    @Transactional(readOnly = true)
    public AnnouncementAdminListResponseDto findAllAnnouncement(Pageable pageable) {
        Page<Announcement> allAnnouncements = announcementAdminRepository.findAll(pageable);
        Page<AnnouncementAdminResponseDto> responseDtos = allAnnouncements.map(AnnouncementAdminResponseDto::new);

        return new AnnouncementAdminListResponseDto(responseDtos.getContent(),
                responseDtos.getTotalPages());
    }

    @Transactional
    public void addAnnouncement(AnnouncementAdminAddDto addDto){
        if (findAnnouncementExist() == true)
            throw new AdminException("유효 공지가 있습니다.", ErrorCode.BAD_REQUEST);

        Announcement announcementAdmin = Announcement.builder()
                .content(addDto.getContent())
                .creatorIntraId(addDto.getCreatorIntraId())
                .build();
        announcementAdminRepository.save(announcementAdmin);
    }

    @Transactional
    public void modifyAnnouncementIsDel(String deleterIntraId) {
        if (findAnnouncementExist() == false)
            throw new AdminException("유효 공지가 없습니다.", ErrorCode.BAD_REQUEST);

        Announcement announcement = announcementAdminRepository.findFirstByOrderByIdDesc();
        announcement.update(deleterIntraId, LocalDateTime.now());
    }

    private Boolean findAnnouncementExist() {
        Announcement announcement = announcementAdminRepository.findFirstByOrderByIdDesc();
        if (announcement == null)
            return false;
        else if (announcement.getDeletedAt() == null)
            return true;

        return false;
    }

}
