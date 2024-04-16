package gg.pingpong.api.user.rank.controller;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.pingpong.api.user.rank.controller.response.ExpRankPageResponseDto;
import gg.pingpong.api.user.rank.controller.response.RankPageResponseDto;
import gg.pingpong.api.user.rank.service.RankService;
import gg.utils.dto.PageRequestDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong")
public class RankController {

	private final RankService rankService;

	@GetMapping("/exp")
	public ExpRankPageResponseDto getExpRankPage(@Valid PageRequestDto pageRequestDto,
		@Parameter(hidden = true) @Login UserDto user) {
		PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize(),
			Sort.by("totalExp").descending().and(Sort.by("intraId")));
		return rankService.getExpRankPageByRedis(pageRequest, user);
	}

	/**
	 *
	 * @param pageRequestDto
	 * @param user
	 * @param season
	 * @param gameType
	 *
	 *  gameType는 single로 고정되어 오고있는데 현재 rank게임은 single만 구현되어있어서 사용 안하기로
	 */
	@GetMapping("/ranks/{gameType}")
	public RankPageResponseDto getRankPage(@Valid PageRequestDto pageRequestDto,
		@Parameter(hidden = true) @Login UserDto user,
		Long season, String gameType) {
		PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize());
		return rankService.getRankPage(pageRequest, user, season);
	}
}
