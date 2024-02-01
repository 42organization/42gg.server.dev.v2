package com.gg.server.domain.announcement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.data.manage.Announcement;
import com.gg.server.domain.announcement.data.AnnouncementRepository;
import com.gg.server.domain.announcement.dto.AnnouncementDto;
import com.gg.server.domain.announcement.exception.AnnounceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
	private final AnnouncementRepository announcementRepository;

	@Transactional(readOnly = true)
	public AnnouncementDto findLastAnnouncement() {
		Announcement announcement = announcementRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new AnnounceNotFoundException());
		if (announcement.getDeletedAt() != null) {
			return new AnnouncementDto("");
		}

		return AnnouncementDto.from(announcement);
	}
}
