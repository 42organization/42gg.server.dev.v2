package com.gg.server.admin.announcement.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.announcement.data.AnnouncementAdminRepository;
import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminResponseDto;
import com.gg.server.domain.announcement.data.Announcement;
import com.gg.server.domain.announcement.exception.AnnounceDupException;
import com.gg.server.domain.announcement.exception.AnnounceNotFoundException;

import lombok.AllArgsConstructor;

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
	public void addAnnouncement(AnnouncementAdminAddDto addDto) {
		if (findAnnouncementExist() == true) {
			throw new AnnounceDupException();
		}

		Announcement announcementAdmin = Announcement.from(addDto);

		announcementAdminRepository.save(announcementAdmin);
	}

	@Transactional
	public void modifyAnnouncementIsDel(String deleterIntraId) {
		if (findAnnouncementExist() == false) {
			throw new AnnounceNotFoundException();
		}

		Announcement announcement = announcementAdminRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new AnnounceNotFoundException());
		announcement.update(deleterIntraId, LocalDateTime.now());
	}

	private Boolean findAnnouncementExist() {
		Announcement announcement = announcementAdminRepository.findFirstByOrderByIdDesc()
			.orElseThrow(() -> new AnnounceNotFoundException());

		if (announcement.getDeletedAt() == null) {
			return true;
		}

		return false;
	}

}
