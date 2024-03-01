package gg.party.api.user.room.service;

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

		List<Room> notStartedRooms = roomRepository.findByStatus(RoomType.OPEN, sortForNotStarted);
		List<Room> startedRooms = roomRepository.findByStatus(RoomType.START, sortForStarted);

		notStartedRooms.addAll(startedRooms);
		List<RoomResDto> roomListResDto = notStartedRooms.stream()
			.map(room -> new RoomResDto(room))
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 시간이 지나 보이지 않게 된 방을 모두 조회한다
	 * @return 끝난 방 전체 List
	 */
	public RoomListResDto findOrderHistoryRoomList() {
		Sort sortForPlayed = Sort.by("dueDate").ascending();

		List<Room> playedRooms = roomRepository.findByStatus(RoomType.OPEN, sortForPlayed);

		List<RoomResDto> roomListResDto = playedRooms.stream()
			.map(room -> new RoomResDto(room))
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}
}
