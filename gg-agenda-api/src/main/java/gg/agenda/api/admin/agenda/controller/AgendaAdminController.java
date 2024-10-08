package gg.agenda.api.admin.agenda.controller;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gg.agenda.api.admin.agenda.controller.request.AgendaAdminUpdateReqDto;
import gg.agenda.api.admin.agenda.controller.response.AgendaAdminResDto;
import gg.agenda.api.admin.agenda.controller.response.AgendaAdminSimpleResDto;
import gg.agenda.api.admin.agenda.service.AgendaAdminService;
import gg.data.agenda.Agenda;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import gg.utils.exception.custom.InvalidParameterException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agenda/admin")
@RequiredArgsConstructor
public class AgendaAdminController {

	private final AgendaAdminService agendaAdminService;

	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Agenda 요청 리스트 조회 성공")})
	@GetMapping("/request/list")
	public ResponseEntity<PageResponseDto<AgendaAdminResDto>> agendaList(
		@ModelAttribute @Valid PageRequestDto pageDto) {
		int page = pageDto.getPage();
		int size = pageDto.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<Agenda> agendaRequestList = agendaAdminService.getAgendaRequestList(pageable);

		List<AgendaAdminResDto> agendaDtos = agendaRequestList.stream()
			.map(AgendaAdminResDto.MapStruct.INSTANCE::toAgendaAdminResDto)
			.collect(Collectors.toList());
		PageResponseDto<AgendaAdminResDto> pageResponseDto = PageResponseDto.of(
			agendaRequestList.getTotalElements(), agendaDtos);
		return ResponseEntity.ok(pageResponseDto);
	}

	@GetMapping("/list")
	public ResponseEntity<List<AgendaAdminSimpleResDto>> agendaSimpleList() {
		List<AgendaAdminSimpleResDto> agendas = agendaAdminService.getAgendaSimpleList();
		return ResponseEntity.status(HttpStatus.OK).body(agendas);
	}

	@ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Agenda 수정 성공"),
		@ApiResponse(responseCode = "400", description = "Agenda 수정 요청이 잘못됨"),
		@ApiResponse(responseCode = "404", description = "Agenda를 찾을 수 없음"),
		@ApiResponse(responseCode = "409", description = "Agenda 지역을 변경할 수 없음"),
		@ApiResponse(responseCode = "409", description = "Agenda 팀 제한을 변경할 수 없음"),
		@ApiResponse(responseCode = "409", description = "Agenda 팀 인원 제한을 변경할 수 없음")})
	@PostMapping("/request")
	public ResponseEntity<Void> agendaUpdate(@RequestParam("agenda_key") UUID agendaKey,
		@ModelAttribute @Valid AgendaAdminUpdateReqDto agendaDto,
		@RequestParam(required = false) MultipartFile agendaPoster) {
		if (Objects.nonNull(agendaPoster) && agendaPoster.getSize() > 1024 * 1024 * 2) {	// 2MB
			throw new InvalidParameterException(AGENDA_POSTER_SIZE_TOO_LARGE);
		}
		agendaAdminService.updateAgenda(agendaKey, agendaDto, agendaPoster);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
