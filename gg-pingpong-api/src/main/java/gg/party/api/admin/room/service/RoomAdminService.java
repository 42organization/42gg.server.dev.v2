package gg.party.api.admin.room.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.repo.party.RoomRepository;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomSameStatusException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomAdminService {
	private final RoomRepository roomRepository;

	/**
	 * 방 Status 변경
	 * @param roomId 방 id
	 * @param newStatus 바꿀 status
	 * @exception RoomNotFoundException 유효하지 않은 방 입력
	 * @exception RoomSameStatusException 같은 상태로 변경
	 */
	@Transactional
	public void modifyRoomStatus(Long roomId, RoomType newStatus) {
		Room room = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);

		if (room.getStatus() == newStatus) {
			throw new RoomSameStatusException();
		}

		room.updateRoomStatus(newStatus);
		roomRepository.save(room);
	}
}
