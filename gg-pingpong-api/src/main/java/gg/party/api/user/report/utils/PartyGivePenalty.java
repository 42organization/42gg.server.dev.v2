// package gg.party.api.user.report.utils;
//
// import java.time.LocalDateTime;
// import java.util.Optional;
//
// import org.springframework.transaction.annotation.Transactional;
//
// import gg.data.manage.Penalty;
// import gg.data.manage.redis.RedisPenaltyUser;
// import gg.data.manage.type.PenaltyType;
// import gg.data.user.User;
// import gg.repo.user.UserRepository;
//
// public class PartyGivePenalty {
//
// 	private final UserRepository userRepository;
//
// 	public PartyGivePenalty(UserRepository userRepository) {
// 		this.userRepository = userRepository;
// 	}
//
// 	/**
// 	 * 패널티 부여
// 	 * @param reporteeId 신고당한 유저 아이디
// 	 * @param roomId 방 번호
// 	 */
// 	@Transactional
// 	public void partyGivePenalty(String intraId, Integer penaltyTime, String reason) {
// 		User user = userRepository.findByIntraId(intraId).get();
// 		Optional<RedisPenaltyUser> redisPenaltyUser = penaltyUserAdminRedisRepository.findByIntraId(intraId);
// 		LocalDateTime releaseTime;
// 		RedisPenaltyUser penaltyUser;
// 		Penalty penalty;
// 		LocalDateTime now = LocalDateTime.now();
// 		if (redisPenaltyUser.isPresent()) {
// 			releaseTime = redisPenaltyUser.get().getReleaseTime().plusHours(penaltyTime);
// 			penaltyUser = new RedisPenaltyUser(intraId, redisPenaltyUser.get().getPenaltyTime() + penaltyTime * 60,
// 				releaseTime, redisPenaltyUser.get().getStartTime(), reason);
// 			penalty = new Penalty(user, PenaltyType.NOSHOW, reason, redisPenaltyUser.get().getReleaseTime(),
// 				penaltyTime * 60);
// 		} else {
// 			releaseTime = now.plusHours(penaltyTime);
// 			penaltyUser = new RedisPenaltyUser(intraId, penaltyTime * 60, releaseTime, now, reason);
// 			penalty = new Penalty(user, PenaltyType.NOSHOW, reason, now, penaltyTime * 60);
// 		}
// 		penaltyRepository.save(penalty);
// 		penaltyUserAdminRedisRepository.addPenaltyUser(penaltyUser, releaseTime);
// 	}
// }
