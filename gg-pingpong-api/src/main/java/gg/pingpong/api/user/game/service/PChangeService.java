package gg.pingpong.api.user.game.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.PChange;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.pchange.PChangeRepository;
import gg.pingpong.utils.exception.pchange.PChangeNotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PChangeService {
	private final PChangeRepository pChangeRepository;

	/**
	 * PPP 변화를 저장 한다.
	 *
	 * @param game      게임
	 * @param user      유저
	 * @param pppResult PPP 변화량
	 * @param isChecked 확인 여부
	 */
	@Transactional
	public void addPChange(Game game, User user, Integer pppResult, Boolean isChecked) {
		pChangeRepository.save(new PChange(game, user, pppResult, isChecked));
	}

	/**
	 * 경험치 변화 이력을 조회 한다.
	 *
	 * @param gameId 게임 아이디
	 * @param userId 유저 아이디
	 * @return 경험치 변화 이력
	 */
	public List<PChange> findExpChangeHistory(Long gameId, Long userId) {
		List<PChange> pChanges = pChangeRepository.findExpHistory(userId, gameId);
		if (pChanges.isEmpty()) {
			throw new PChangeNotExistException();
		}
		return pChanges;
	}

	/**
	 * PPP 변화 이력을 조회 한다.
	 *
	 * @param gameId   게임 아이디
	 * @param userId   유저 아이디
	 * @param seasonId 시즌 아이디
	 * @return PPP 변화 이력
	 */
	public List<PChange> findPPPChangeHistory(Long gameId, Long userId, Long seasonId) {
		List<PChange> pChanges = pChangeRepository.findPPPHistory(userId, gameId, seasonId);
		if (pChanges.isEmpty()) {
			throw new PChangeNotExistException();
		}
		return pChanges;
	}
}
