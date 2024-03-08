package gg.party.api.user.report.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.auth.UserDto;
import gg.data.party.Room;
import gg.data.party.RoomReport;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.party.api.user.report.request.ReportRoomReqDto;
import gg.repo.party.RoomReportRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.AlredayReportedRoomException;
import gg.utils.exception.party.RoomNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final UserRoomRepository userRoomRepository;
	private final RoomReportRepository roomReportRepository;

	/**
	 * 방을 신고한다.
	 * @param roomId 방 번호
	 * @param reportRoomReqDto 신고 내용
	 * @param user 신고자
	 * @exception RoomNotFoundException 방을 찾을 수 없음
	 * @exception AlredayReportedRoomException 이미 신고한 방
	 * @return 방 번호
	 */
	@Transactional
	public Long addReportRoom(Long roomId, ReportRoomReqDto reportRoomReqDto, UserDto user) {
		Room targetRoom = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		User userEntity = userRepository.findById(user.getId()).get();
		Optional<RoomReport> existingReport = roomReportRepository.findByReporterAndRoomId(userEntity,
			targetRoom.getId());
		if (existingReport.isPresent()) {
			throw new AlredayReportedRoomException();
		}
		RoomReport roomReport = new RoomReport(userEntity, targetRoom.getCreator(), targetRoom,
			reportRoomReqDto.getContent());
		roomReportRepository.save(roomReport);

		List<RoomReport> allReportRoom = roomReportRepository.findByRoomId(targetRoom.getId());
		if (allReportRoom.size() == 3) {
			targetRoom.updateStatus(RoomType.HIDDEN);
			roomRepository.save(targetRoom);
		}
		// 사용자 정지
		//partyGivePenalty(targetRoom.getCreator().getIntraId(), 24, "신고 3회 이상");
		return roomId;
	}
}
