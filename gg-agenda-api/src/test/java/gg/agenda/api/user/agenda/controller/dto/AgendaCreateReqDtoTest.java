package gg.agenda.api.user.agenda.controller.dto;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.InvalidParameterException;

@UnitTest
class AgendaCreateReqDtoTest {

	@Test
	@DisplayName("Agenda 생성 성공")
	void createAgendaSuccess() {
		//given
		UserDto user = UserDto.builder().intraId("intraId").build();
		AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
			.agendaDeadLine(LocalDateTime.now().plusDays(5))
			.agendaStartTime(LocalDateTime.now().plusDays(8))
			.agendaEndTime(LocalDateTime.now().plusDays(10))
			.build();

		// when
		Agenda agenda = AgendaCreateReqDto.MapStruct.INSTANCE.toEntity(dto, user);

		// then
		assertNotNull(agenda);
		assertThat(agenda.getHostIntraId()).isEqualTo(user.getIntraId());
	}
}
