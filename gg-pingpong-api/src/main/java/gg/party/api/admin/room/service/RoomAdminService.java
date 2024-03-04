package gg.party.api.admin.room.service;

import gg.admin.repo.party.RoomAdminRepository;
import gg.data.party.type.RoomType;
import gg.data.party.Room; // Assuming this entity class exists
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomAdminService {

	private final RoomAdminRepository roomAdminRepository; // Assume this repository interface is correctly set up

	@Transactional
	public void updateRoomStatus(Long roomId, RoomType newStatus) {
		Room room = roomAdminRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

		room.UpdateRoomStatus(newStatus);
		roomAdminRepository.save(room);
	}
}
