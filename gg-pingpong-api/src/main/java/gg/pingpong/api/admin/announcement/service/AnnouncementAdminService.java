package gg.pingpong.api.admin.announcement.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.admin.repo.announcement.AnnouncementAdminRepository;
import gg.pingpong.api.admin.announcement.dto.AnnouncementAdminAddDto;
import gg.pingpong.api.admin.announcement.dto.AnnouncementAdminListResponseDto;
import gg.pingpong.api.admin.announcement.dto.AnnouncementAdminResponseDto;
import gg.pingpong.data.manage.Announcement;
import gg.pingpong.utils.exception.announcement.AnnounceDupException;
import gg.pingpong.utils.exception.announcement.AnnounceNotFoundException;
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
		Announcement announcement = announcementAdminRepository.findFirstByOrderByIdDesc()
			.orElseThrow(AnnounceNotFoundException::new);
		if (announcement.getDeletedAt() == null) {
			throw new AnnounceDupException();
		}

		announcementAdminRepository.save(Announcement.from(addDto.getContent(), addDto.getCreatorIntraId()));
	}

	@Transactional
	public void modifyAnnouncementIsDel(String deleterIntraId) {
		Announcement announcement = announcementAdminRepository.findFirstByOrderByIdDesc()
			.orElseThrow(AnnounceNotFoundException::new);
		if (announcement.getDeletedAt() != null) {
			throw new AnnounceNotFoundException();
		}
		announcement.update(deleterIntraId, LocalDateTime.now());
	}
}
