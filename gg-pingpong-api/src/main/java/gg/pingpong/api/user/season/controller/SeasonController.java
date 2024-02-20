package gg.pingpong.api.user.season.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.domain.season.dto.SeasonListResDto;
import com.gg.server.domain.season.service.SeasonService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pingpong/")
public class SeasonController {

	private final SeasonService seasonService;

	@GetMapping("seasons")
	public SeasonListResDto seasonList() {
		return new SeasonListResDto(seasonService.seasonList());
	}
}
