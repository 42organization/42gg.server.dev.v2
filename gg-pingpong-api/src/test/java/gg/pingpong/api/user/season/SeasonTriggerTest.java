package gg.pingpong.api.user.season;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.pingpong.game.type.Mode;
import gg.data.pingpong.season.Season;
import gg.repo.season.SeasonRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Slf4j
public class SeasonTriggerTest {

	@Autowired
	MockMvc mvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	EntityManager em;

	@Autowired
	private SeasonRepository seasonRepository;

	@BeforeEach
	@Transactional
	public void init() {
		System.out.println("before each");
		Season s1 = new Season("test1 시즌", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), 1000, 100);
		seasonRepository.save(s1);
		seasonRepository.save(
			new Season("test2 season", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), 1000, 100));
		seasonRepository.flush();
		System.out.println(seasonRepository.findAll());
	}

	@Test
	@DisplayName("시즌 삭제 방지 Test")
	@Transactional
	public void seasonDeleteImpossibleTest() {
		Season season = new Season("test1 시즌", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), 1000, 100);
		seasonRepository.save(season);
		Long id = season.getId();
		testDataUtils.createMockMatch(testDataUtils.createNewUser(), season,
			LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(5), Mode.RANK);
		log.info("ID : " + id);
		Throwable thrownException = Assertions.assertThrows(UndeclaredThrowableException.class, () -> {
			seasonRepository.deleteById(id);
			em.flush();
		});

		log.info("에러 메시지: " + thrownException.getMessage());
	}
}
