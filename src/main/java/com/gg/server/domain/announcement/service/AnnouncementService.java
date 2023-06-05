package com.gg.server.domain.announcement.service;

import com.gg.server.domain.announcement.data.Announcement;
import com.gg.server.domain.announcement.data.AnnouncementRepository;
import com.gg.server.domain.announcement.dto.AnnouncementDto;
import com.gg.server.domain.announcement.exception.AnnounceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    @Transactional(readOnly = true)
    public AnnouncementDto findLastAnnouncement() {
        Announcement announcement = announcementRepository.findFirstByOrderByIdDesc().orElseThrow(() -> new AnnounceNotFoundException());
        if (announcement.getDeletedAt() != null)
            throw new AnnounceNotFoundException();

        return AnnouncementDto.from(announcement);
    }
}
