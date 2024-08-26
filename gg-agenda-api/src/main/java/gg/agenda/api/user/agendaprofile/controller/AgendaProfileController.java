package gg.agenda.api.user.agendaprofile.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agendaprofile.controller.request.AgendaProfileChangeReqDto;
import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileDetailsResDto;
import gg.agenda.api.user.agendaprofile.controller.response.MyAgendaProfileDetailsResDto;
import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileInfoDetailsResDto;
import gg.agenda.api.user.agendaprofile.controller.response.AttendedAgendaListResDto;
import gg.agenda.api.user.agendaprofile.controller.response.CurrentAttendAgendaListResDto;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileFindService;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileService;
import gg.agenda.api.user.agendaprofile.service.IntraProfileUtils;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfile;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.user.type.RoleType;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/profile")
public class AgendaProfileController {
	private final AgendaProfileFindService agendaProfileFindService;
	private final AgendaProfileService agendaProfileService;
	private final TicketService ticketService;
	private final IntraProfileUtils intraProfileUtils;

	/**
	 * AgendaProfile admin 여부 조회 API
	 * @param user 로그인한 사용자 정보
	 */
	@GetMapping("/info")
	public ResponseEntity<AgendaProfileInfoDetailsResDto> myAgendaProfileInfoDetails(
		@Login @Parameter(hidden = true) UserDto user) {
		String intraId = user.getIntraId();
		Boolean isAdmin = user.getRoleType() == RoleType.ADMIN;

		AgendaProfileInfoDetailsResDto agendaProfileInfoDetails = new AgendaProfileInfoDetailsResDto(intraId, isAdmin);

		return ResponseEntity.ok(agendaProfileInfoDetails);
	}

	/**
	 * AgendaProfile 상세 조회 API
	 * @param user 로그인한 사용자 정보
	 * @return AgendaProfileDetailsResDto 객체와 HTTP 상태 코드를 포함한 ResponseEntity
	 */
	@GetMapping
	public ResponseEntity<MyAgendaProfileDetailsResDto> myAgendaProfileDetails(
		@Login @Parameter(hidden = true) UserDto user) {
		AgendaProfile profile = agendaProfileFindService.findAgendaProfileByIntraId(user.getIntraId());
		int ticketCount = ticketService.findTicketList(profile).size();
		IntraProfile intraProfile = intraProfileUtils.getIntraProfile();
		MyAgendaProfileDetailsResDto agendaProfileDetails = MyAgendaProfileDetailsResDto.toDto(
			profile, ticketCount, intraProfile);
		return ResponseEntity.ok(agendaProfileDetails);
	}

	/**
	 * AgendaProfile 변경 API
	 * @param user  로그인한 사용자 정보
	 * @param reqDto 변경할 프로필 정보
	 * @return HTTP 상태 코드와 빈 응답
	 */
	@PatchMapping
	public ResponseEntity<Void> agendaProfileModify(@Login @Parameter(hidden = true) UserDto user,
		@RequestBody @Valid AgendaProfileChangeReqDto reqDto) {
		agendaProfileService.modifyAgendaProfile(user.getId(), reqDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 현재 참여중인 Agenda 목록 조회하는 메서드
	 * @param user 로그인한 유저의 id
	 * @return List<CurrentAttendAgendaListResDto> 객체
	 */
	@GetMapping("/current/list")
	public ResponseEntity<List<CurrentAttendAgendaListResDto>> getCurrentAttendAgendaList(
		@Login @Parameter(hidden = true) UserDto user) {
		List<CurrentAttendAgendaListResDto> currentAttendAgendaList = agendaProfileFindService
			.findCurrentAttendAgenda(user.getIntraId());
		return ResponseEntity.ok(currentAttendAgendaList);
	}

	@GetMapping("/{intraId}")
	public ResponseEntity<AgendaProfileDetailsResDto> agendaProfileDetails(@PathVariable String intraId) {
		AgendaProfile profile = agendaProfileFindService.findAgendaProfileByIntraId(intraId);
		IntraProfile intraProfile = intraProfileUtils.getIntraProfile(intraId);
		AgendaProfileDetailsResDto resDto = AgendaProfileDetailsResDto.toDto(profile, intraProfile);
		return ResponseEntity.ok(resDto);
	}

	/**
	 * 과거에 참여했던 Agenda 목록 조회하는 메서드
	 * @param pageRequest 페이지네이션 요청 정보, agendaId 아젠다 아이디
	 */
	@GetMapping("/history/list/{intraId}")
	public ResponseEntity<PageResponseDto<AttendedAgendaListResDto>> getAttendedAgendaList(
		@PathVariable String intraId, @ModelAttribute @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<AgendaTeamProfile> attendedAgendaList = agendaProfileFindService
			.findAttendedAgenda(intraId, pageable);

		List<AttendedAgendaListResDto> attendedAgendaDtos = attendedAgendaList.stream()
			.map(agendaTeamProfile -> {
				List<AgendaTeamProfile> teamMates = agendaProfileFindService
					.findTeamMatesFromAgendaTeam(agendaTeamProfile.getAgendaTeam());
				return new AttendedAgendaListResDto(agendaTeamProfile, teamMates);
			})
			.collect(Collectors.toList());

		PageResponseDto<AttendedAgendaListResDto> pageResponseDto = PageResponseDto.of(
			attendedAgendaList.getTotalElements(), attendedAgendaDtos);
		return ResponseEntity.ok(pageResponseDto);
	}
}
