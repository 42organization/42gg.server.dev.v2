package gg.pingpong.api.user.manage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.user.manage.dto.AnnouncementDto;
import gg.pingpong.data.manage.Announcement;
import gg.pingpong.repo.manage.AnnouncementRepository;
import gg.pingpong.utils.exception.announcement.AnnounceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
	private final AnnouncementRepository announcementRepository;

	/**
	 * <p>가장 최근 공지를 찾아서 dto로 반환해준다.</p>
	 * <p>만약 가장 최근 공지가 삭제 되었다면 dto에 빈값을 넣어서 반환해준다.</p>
	 * @throws AnnounceNotFoundException 공지 없음
	 * @return AnnouncementDto
	 */
	@Transactional(readOnly = true)
	public AnnouncementDto findLastAnnouncement() {
		Announcement announcement = announcementRepository.findFirstByOrderByIdDesc()
			.orElseThrow(AnnounceNotFoundException::new);
		if (announcement.getDeletedAt() != null) {
			return new AnnouncementDto("");
		}

		return AnnouncementDto.from(announcement);
	}
}
