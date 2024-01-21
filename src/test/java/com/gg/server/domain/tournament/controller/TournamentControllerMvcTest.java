package com.gg.server.domain.tournament.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.gg.server.domain.tournament.dto.TournamentFilterRequestDto;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.dto.TournamentUserRegistrationResponseDto;
import com.gg.server.domain.tournament.service.TournamentService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.config.WebConfig;
import com.gg.server.global.security.config.SecurityConfig;
import com.gg.server.global.security.jwt.utils.TokenAuthenticationFilter;
import com.gg.server.global.utils.querytracker.LoggingInterceptor;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@WebMvcTest(controllers = TournamentController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TokenAuthenticationFilter.class),
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = LoggingInterceptor.class)})
class TournamentControllerMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TournamentService tournamentService;

	@Autowired
	private TournamentController tournamentController;

	@Nested
	@DisplayName("getAllTournamentList")
	class getAllTournamentList {

		@DisplayName("페이지번호가 1보다 작을 경우 에러 발생")
		@ParameterizedTest()
		@ValueSource(ints = {-1, 0})
		void pageMustGreaterThanZero(Integer page) {
			//Arrange
			TournamentFilterRequestDto dto = new TournamentFilterRequestDto(page, 1, null, null);

			//Act, Assert
			assertThatThrownBy(() -> tournamentController.getAllTournamentList(dto))
				.isInstanceOf(ConstraintViolationException.class);
		}

		@DisplayName("사이즈가 1과 30 사이가 아닐경우 에러 발생")
		@ParameterizedTest()
		@ValueSource(ints = {-1, 0, 31})
		void sizeError(Integer size) {
			//Arrange
			TournamentFilterRequestDto dto = new TournamentFilterRequestDto(1, size, null, null);

			//Act, Assert
			assertThatThrownBy(() -> tournamentController.getAllTournamentList(dto))
				.isInstanceOf(ConstraintViolationException.class);
		}

		@DisplayName("Success")
		@Test
		void success() {
			//Arrange
			TournamentFilterRequestDto dto = new TournamentFilterRequestDto(1, 1, null, null);
			TournamentListResponseDto resultDto = Mockito.mock(TournamentListResponseDto.class);
			when(tournamentService.getAllTournamentList(any(), any(), any())).thenReturn(resultDto);

			//Act
			ResponseEntity<TournamentListResponseDto> response = tournamentController.getAllTournamentList(dto);

			//Assert
			assertThat(response.getStatusCodeValue()).isEqualTo(200);
			assertThat(response.getBody()).isEqualTo(resultDto);
		}
	}

	@Nested
	@DisplayName("getUserStatusInTournament")
	class getUserStatusInTournament {
		@DisplayName("Success")
		@Test
		void success() {
			//Arrange
			UserDto userDto = Mockito.mock(UserDto.class);
			TournamentUserRegistrationResponseDto resultDto = Mockito.mock(TournamentUserRegistrationResponseDto.class);
			when(tournamentService.getUserStatusInTournament(any(), any())).thenReturn(resultDto);

			//Act
			ResponseEntity<TournamentUserRegistrationResponseDto> response = tournamentController
				.getUserStatusInTournament(1L, userDto);

			//Assert
			assertThat(response.getStatusCodeValue()).isEqualTo(200);
			assertThat(response.getBody()).isEqualTo(resultDto);
		}
	}

	@Nested
	@DisplayName("getTournnament")
	class getTournnament {
		@DisplayName("id가 양수가 아닐경우 에러 발생")
		@ParameterizedTest()
		@ValueSource(longs = {-1, 0})
		void idGreaterThanZero(Long id) {
			//Act, Assert
			assertThatThrownBy(() -> tournamentController.getTournnament(id))
				.isInstanceOf(ConstraintViolationException.class);
		}

		@DisplayName("Success")
		@Test
		void success() {
			//Arrange
			TournamentResponseDto resultDto = Mockito.mock(TournamentResponseDto.class);
			when(tournamentService.getTournament(anyLong())).thenReturn(resultDto);

			//Act
			ResponseEntity<TournamentResponseDto> response = tournamentController.getTournnament(1L);

			//Assert
			assertThat(response.getStatusCodeValue()).isEqualTo(200);
			assertThat(response.getBody()).isEqualTo(resultDto);
		}
	}

	@Nested
	@DisplayName("cancelTournamentUserRegistration")
	class cancelTournamentUserRegistration {

		@DisplayName("Success")
		@Test
		void success() {
			//Arrange
			UserDto userDto = Mockito.mock(UserDto.class);
			TournamentUserRegistrationResponseDto resultDto = Mockito.mock(TournamentUserRegistrationResponseDto.class);
			when(tournamentService.cancelTournamentUserRegistration(anyLong(), any())).thenReturn(resultDto);

			//Act
			ResponseEntity<TournamentUserRegistrationResponseDto> response;
			response = tournamentController.cancelTournamentUserRegistration(1L, userDto);

			//Assert
			assertThat(response.getStatusCodeValue()).isEqualTo(200);
			assertThat(response.getBody()).isEqualTo(resultDto);
		}
	}
}
