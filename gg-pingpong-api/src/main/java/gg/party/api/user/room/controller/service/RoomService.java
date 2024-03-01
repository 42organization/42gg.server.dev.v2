package gg.party.api.user.room.controller.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.controller.response.RoomResDto;
import gg.repo.party.RoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	private final RoomRepository roomRepository;

	/**
	 * 시작하지 않은 방과 시작한 방을 모두 조회한다
	 * @return 시작하지 않은 방 (최신순) + 시작한 방(끝나는 시간이 빠른 순) 전체 List
	 */
	public RoomListResDto findOrderRoomList() {
		Sort sortForNotStarted = Sort.by("createdAt").descending();
		Sort sortForStarted = Sort.by("dueDate").ascending();

		List<Room> notStartedRooms = roomRepository.findByRoomStatus(RoomType.OPEN, sortForNotStarted);
		List<Room> startedRooms = roomRepository.findByRoomStatus(RoomType.START, sortForStarted);

		notStartedRooms.addAll(startedRooms);
		List<RoomResDto> roomResDtoList = notStartedRooms.stream()
			.map(room -> new RoomResDto(room))
			.collect(Collectors.toList());

		return new RoomListResDto(roomResDtoList);
	}
}
