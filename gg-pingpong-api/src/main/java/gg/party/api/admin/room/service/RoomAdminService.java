package gg.party.api.admin.room.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.repo.party.RoomRepository;
import gg.utils.exception.party.RoomAlreadyHiddenException;
import gg.utils.exception.party.RoomNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomAdminService {
	private final RoomRepository roomRepository;

	@Transactional
	public void modifyRoomStatus(Long roomId, RoomType newStatus) {
		Room room = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);

		if (RoomType.HIDDEN == room.getStatus() && RoomType.HIDDEN == newStatus) {
			throw new RoomAlreadyHiddenException();
		}

		room.updateRoomStatus(newStatus);
		roomRepository.save(room);
	}
}
