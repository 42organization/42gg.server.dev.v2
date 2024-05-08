package gg.party.api.admin.room.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.repo.party.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomStatusService {
	private final RoomRepository roomRepository;

	/**
	 * 방 FINISH 변경 스케쥴러
	 * 시작한 방(START) 2시간후에 FINISH로 변경
	 */
	@Transactional
	public void finishStartedRooms() {
		LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
		List<Room> startedRooms = roomRepository.findByStatusAndStartDate(RoomType.START, twoHoursAgo);
		for (Room room : startedRooms) {
			room.roomFinish();
			roomRepository.save(room);
			log.info("{}번 방이 종료되었습니다.", room.getId());
		}
	}

	/**
	 * 방 FAIL 변경 스케쥴러
	 * due date 지났는데 시작 안했으면 FAIL로 변경
	 */
	@Transactional
	public void failOpenedRooms() {
		LocalDateTime now = LocalDateTime.now();
		List<Room> openRooms = roomRepository.findByStatus(RoomType.OPEN, null);
		for (Room room : openRooms) {
			if (room.getDueDate().isBefore(now)) {
				room.roomFail();
				roomRepository.save(room);
				log.info("{}번 방이 시간이 지나 Fail되었습니다.", room.getId());
			}
		}
	}
}
