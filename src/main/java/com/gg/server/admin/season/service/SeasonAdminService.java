package com.gg.server.admin.season.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.rank.service.RankAdminService;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
import com.gg.server.data.game.Season;
import com.gg.server.domain.season.exception.SeasonForbiddenException;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.season.exception.SeasonTimeBeforeException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SeasonAdminService {
	private final SeasonAdminRepository seasonAdminRepository;
	private final RankRedisAdminService rankRedisAdminService;
	private final RankAdminService rankAdminService;

	/**
	 * <p>모든 시즌을 찾아서 반환해주는 메서드입니다.</p>
	 * @return
	 */
	public List<SeasonAdminDto> findAllSeasons() {
		List<Season> seasons = seasonAdminRepository.findAllByOrderByStartTimeDesc();
		List<SeasonAdminDto> dtoList = new ArrayList<>();
		for (Season season : seasons) {
			SeasonAdminDto dto = new SeasonAdminDto(season);
			dtoList.add(dto);
		}
		return dtoList;
	}

	/**
	 * <p>현재 시간보다 이후의 시간에 시작하는 새로운 시즌을 추가한다.</p>
	 * <p>새로운 시즌을 예약하는 메서드이다. 예약 중인 시즌이 여러개일 수 도 있다.</p>
	 * @param createDto dto
	 */
	@Transactional
	public void createSeason(SeasonCreateRequestDto createDto) {
		Season newSeason = createDto.toEntity();

		insert(newSeason);
		seasonAdminRepository.save(newSeason);

		checkSeasonAtDB();

		SeasonAdminDto seasonAdminDto = new SeasonAdminDto(newSeason);
		rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
		rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
	}

	/**
	 * <p>타겟 시즌을 찾는 메서드입니다.</p>
	 * @param seasonId 타겟 시즌 아이디
	 * @return
	 */
	@Transactional
	public SeasonAdminDto findSeasonById(Long seasonId) {
		Season season = seasonAdminRepository.findById(seasonId).orElseThrow(SeasonNotFoundException::new);

		return new SeasonAdminDto(season);
	}

	/**
	 * <p>예약된 시즌을 삭제한다.</p>
	 * @param seasonId 타겟 예약 시즌 아이디
	 */
	@Transactional
	public void deleteSeason(Long seasonId) {
		Season season = seasonAdminRepository.findById(seasonId).orElseThrow(SeasonNotFoundException::new);
		detach(season);
		SeasonAdminDto seasonDto = new SeasonAdminDto(season);
		rankAdminService.deleteAllUserRankBySeason(seasonDto);
		rankRedisAdminService.deleteSeasonRankBySeasonId(seasonId);
		seasonAdminRepository.delete(season);
		checkSeasonAtDB();
	}

	/**
	 * <p>예약된 시즌을 정보를 수정합니다.</p>
	 * <p>현재 시즌은 수정 버튼이 있지만 구현이 안되어 있어서 에러가 발생함. -> 추후 수정해야함</p>
	 * @param seasonId
	 * @param updateDto
	 */
	@Transactional
	public void updateSeason(Long seasonId, SeasonUpdateRequestDto updateDto) {
		Season season = seasonAdminRepository.findById(seasonId).orElseThrow(SeasonNotFoundException::new);

		if (LocalDateTime.now().isAfter(season.getStartTime())) {
			throw new SeasonForbiddenException();
		}
		// 예약 시즌 수정
		detach(season);
		seasonAdminRepository.updateReserveSeasonById(seasonId, updateDto.getSeasonName(), updateDto.getStartTime(),
			updateDto.getStartPpp(), updateDto.getPppGap());
		season = seasonAdminRepository.findById(seasonId).orElseThrow(SeasonNotFoundException::new);
		insert(season);
		seasonAdminRepository.save(season);
		checkSeasonAtDB();

		SeasonAdminDto seasonAdminDto = new SeasonAdminDto(season);
		rankAdminService.deleteAllUserRankBySeason(seasonAdminDto);
		rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
		rankRedisAdminService.deleteSeasonRankBySeasonId(seasonAdminDto.getSeasonId());
		rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);

	}

	/**
	 * <p>추가 하고자하는 season이 현재 시간부터 24시간 후가 아니면 에러</p>
	 * @param season
	 */
	private void insert(Season season) {
		if (LocalDateTime.now().plusHours(24).isAfter(season.getStartTime())) {
			throw new SeasonTimeBeforeException();
		}
		List<Season> beforeSeasons = seasonAdminRepository.findBeforeSeasons(season.getStartTime());
		Season beforeSeason = beforeSeasons.isEmpty() ? null : beforeSeasons.get(0);
		List<Season> afterSeasons = seasonAdminRepository.findAfterSeasons(season.getStartTime());
		Season afterSeason = afterSeasons.isEmpty() ? null : afterSeasons.get(0);

		if (beforeSeason != null) {
			if (beforeSeason.getStartTime().plusDays(1).isAfter(season.getStartTime())) {
				throw new SeasonForbiddenException();
			}
			beforeSeason.setEndTime(season.getStartTime().minusSeconds(1));
		}
		if (afterSeason != null) {
			season.setEndTime(afterSeason.getStartTime().minusSeconds(1));
		} else {
			season.setEndTime(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
		}
	}

	/**
	 * <p>타겟 시즌의 앞뒤 시즌끼리 이어주는 메서드이다</p>
	 * @param season
	 */
	private void detach(Season season) {
		// 이미 해당 시즌 중간 || 이전 시즌
		if ((LocalDateTime.now().isAfter(season.getStartTime()) && LocalDateTime.now().isBefore(season.getEndTime()))
			|| season.getEndTime().isBefore(LocalDateTime.now())) {
			throw new SeasonForbiddenException();
		}

		List<Season> beforeSeasons = seasonAdminRepository.findBeforeSeasons(season.getStartTime());
		Season beforeSeason = beforeSeasons.isEmpty() ? null : beforeSeasons.get(0);
		List<Season> afterSeasons = seasonAdminRepository.findAfterSeasons(season.getStartTime());
		Season afterSeason = afterSeasons.isEmpty() ? null : afterSeasons.get(0);

		if (beforeSeason != null) {
			if (afterSeason != null) {
				beforeSeason.setEndTime(afterSeason.getStartTime().minusSeconds(1));
			} else {
				beforeSeason.setEndTime(
					LocalDateTime.of(9999, 12, 31, 23, 59, 59));
			}
		}
	}

	/**
	 * <p>시즌들의 시작, 종료 시간 체크 메서드이다</p>
	 */
	private void checkSeasonAtDB() {
		List<Season> seasons = seasonAdminRepository.findAllByOrderByStartTimeAsc();
		for (int i = 1; i < seasons.size(); i++) {
			if (isOverlap(seasons.get(i - 1), seasons.get(i))) {
				throw new SeasonForbiddenException();
			}
		}
	}

	/**
	 * <p>시즌끼리의 겹치는 기간이 있는지 판별하느 메서드 입니다.</p>
	 * @param season1
	 * @param season2
	 * @return
	 */
	private boolean isOverlap(Season season1, Season season2) {
		LocalDateTime start1 = season1.getStartTime();
		LocalDateTime end1 = season1.getEndTime();
		LocalDateTime start2 = season2.getStartTime();
		LocalDateTime end2 = season2.getEndTime();

		if (start1.isEqual(end1) || start2.isEqual(end2)) {
			return false;
		}
		// 첫 번째 기간이 두 번째 기간의 이전에 끝날 때
		if (end1.isBefore(start2)) {
			return false;
		}

		// 첫 번째 기간이 두 번째 기간의 이후에 시작할 때
		if (start1.isAfter(end2)) {
			return false;
		}

		// 나머지 경우에는 두 기간이 겹칩니다.
		return true;
	}
}
