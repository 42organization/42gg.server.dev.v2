package gg.party.api.admin.room.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.repo.party.RoomRepository;
import gg.utils.exception.party.RoomAlreadyHiddenException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomAdminService {
	private final RoomRepository roomRepository;

	@Transactional
	public void updateRoomStatus(Long roomId, RoomType newStatus) {
		Room room = roomRepository.findById(roomId)
			.orElseThrow(RoomAlreadyHiddenException::new);

		room.updateRoomStatus(newStatus);
		roomRepository.save(room);
	}
}
