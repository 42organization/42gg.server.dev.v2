package gg.agenda.api.user.agenda.controller.dto;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import gg.agenda.api.user.agenda.controller.dto.AgendaCreateDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.InvalidParameterException;

@UnitTest
class AgendaCreateDtoTest {

	@Test
	@DisplayName("Agenda 생성 성공")
	void createAgendaSuccess() {
		//given
		UserDto user = UserDto.builder().intraId("intraId").build();
		AgendaCreateDto dto = AgendaCreateDto.builder()
			.agendaDeadLine(LocalDateTime.now().plusDays(5))
			.agendaStartTime(LocalDateTime.now().plusDays(8))
			.agendaEndTime(LocalDateTime.now().plusDays(10))
			.build();

		// when
		Agenda agenda = AgendaCreateDto.MapStruct.INSTANCE.toEntity(dto, user);

		// then
		assertNotNull(agenda);
		assertThat(agenda.getHostIntraId()).isEqualTo(user.getIntraId());
	}

	@Nested
	@DisplayName("Agenda 생성 실패")
	class CreateAgendaFailed {

		@Test
		@DisplayName("deadline이 start time보다 늦을 경우")
		void createAgendaFailedWhenDeadlineIsBeforeStartTime() {
			//given
			UserDto user = UserDto.builder().intraId("intraId").build();
			AgendaCreateDto dto = AgendaCreateDto.builder()
				.agendaDeadLine(LocalDateTime.now().plusDays(5))
				.agendaStartTime(LocalDateTime.now().plusDays(2))
				.agendaEndTime(LocalDateTime.now().plusDays(7))
				.build();

			// expected
			assertThrows(InvalidParameterException.class,
				() -> AgendaCreateDto.MapStruct.INSTANCE.toEntity(dto, user));
		}

		@Test
		@DisplayName("deadline이 end time보다 늦을 경우")
		void createAgendaFailedWhenDeadlineIsBeforeEndTime() {
			//given
			UserDto user = UserDto.builder().intraId("intraId").build();
			AgendaCreateDto dto = AgendaCreateDto.builder()
				.agendaDeadLine(LocalDateTime.now().plusDays(5))
				.agendaStartTime(LocalDateTime.now().plusDays(7))
				.agendaEndTime(LocalDateTime.now().plusDays(6))
				.build();

			// expected
			assertThrows(InvalidParameterException.class,
				() -> AgendaCreateDto.MapStruct.INSTANCE.toEntity(dto, user));
		}

		@Test
		@DisplayName("start time이 end time보다 늦을 경우")
		void createAgendaFailedWhenStartTimeIsBeforeEndTime() {
			//given
			UserDto user = UserDto.builder().intraId("intraId").build();
			AgendaCreateDto dto = AgendaCreateDto.builder()
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(6))
				.agendaEndTime(LocalDateTime.now().plusDays(5))
				.build();

			// expected
			assertThrows(InvalidParameterException.class,
				() -> AgendaCreateDto.MapStruct.INSTANCE.toEntity(dto, user));
		}

		@Test
		@DisplayName("min team이 max team보다 큰 경우")
		void createAgendaFailedWhenMinTeamIsGreaterThanMaxTeam() {
			//given
			UserDto user = UserDto.builder().intraId("intraId").build();
			AgendaCreateDto dto = AgendaCreateDto.builder()
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(6))
				.agendaEndTime(LocalDateTime.now().plusDays(8))
				.agendaMinTeam(5).agendaMaxTeam(2)
				.build();

			// expected
			assertThrows(InvalidParameterException.class,
				() -> AgendaCreateDto.MapStruct.INSTANCE.toEntity(dto, user));
		}

		@Test
		@DisplayName("min people이 max people보다 큰 경우")
		void createAgendaFailedWhenMinPeopleIsGreaterThanMaxPeople() {
			//given
			UserDto user = UserDto.builder().intraId("intraId").build();
			AgendaCreateDto dto = AgendaCreateDto.builder()
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(6))
				.agendaEndTime(LocalDateTime.now().plusDays(8))
				.agendaMinPeople(5).agendaMaxPeople(2)
				.build();

			// expected
			assertThrows(InvalidParameterException.class,
				() -> AgendaCreateDto.MapStruct.INSTANCE.toEntity(dto, user));
		}
	}
}