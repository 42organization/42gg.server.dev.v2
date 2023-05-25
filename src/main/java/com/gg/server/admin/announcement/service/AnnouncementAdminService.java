package com.gg.server.admin.announcement.service;

import com.gg.server.admin.announcement.data.AnnouncementAdminRepository;
import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminResponseDto;
import com.gg.server.domain.announcement.Announcement;
import com.gg.server.domain.announcement.exception.AnDupException;
import com.gg.server.domain.announcement.exception.AnNotFoundException;
import com.gg.server.global.exception.ErrorCode;
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
            throw new AnDupException();

        Announcement announcementAdmin = Announcement.from(addDto);

        announcementAdminRepository.save(announcementAdmin);
    }

    @Transactional
    public void modifyAnnouncementIsDel(String deleterIntraId) {
        if (findAnnouncementExist() == false)
            throw new AnNotFoundException();

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
