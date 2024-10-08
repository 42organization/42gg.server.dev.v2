package gg.agenda.api.admin.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.agenda.api.admin.agenda.controller.request.AgendaAdminUpdateReqDto;
import gg.agenda.api.admin.agenda.controller.response.AgendaAdminSimpleResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaPosterImage;
import gg.data.agenda.AgendaTeam;
import gg.repo.agenda.AgendaPosterImageRepository;
import gg.utils.exception.custom.BusinessException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.file.handler.ImageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendaAdminService {

	private final AgendaAdminRepository agendaAdminRepository;

	private final AgendaTeamAdminRepository agendaTeamAdminRepository;

	private final AgendaPosterImageRepository agendaPosterImageRepository;

	private final ImageHandler imageHandler;

	@Value("${info.image.defaultUrl}")
	private String defaultUri;

	@Transactional(readOnly = true)
	public Page<Agenda> getAgendaRequestList(Pageable pageable) {
		return agendaAdminRepository.findAll(pageable);
	}

	@Transactional
	public void updateAgenda(UUID agendaKey, AgendaAdminUpdateReqDto agendaDto, MultipartFile agendaPoster) {
		Agenda agenda = agendaAdminRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		List<AgendaTeam> teams = agendaTeamAdminRepository.findAllByAgenda(agenda);

		try {
			if (Objects.nonNull(agendaPoster) && agendaPoster.getSize() > 0) {
				URL storedUrl = imageHandler.uploadImageOrDefault(agendaPoster, agenda.getTitle(), defaultUri);
				agenda.updatePosterUri(storedUrl.toString());
				Optional<AgendaPosterImage> posterImage = agendaPosterImageRepository.findByAgendaIdAndIsCurrentTrue(
					agenda.getId());
				posterImage.ifPresent(AgendaPosterImage::updateIsCurrentToFalse);
				agendaPosterImageRepository.save(new AgendaPosterImage(agenda.getId(), storedUrl.toString()));
			}
		} catch (IOException e) {
			log.error("Failed to upload image", e);
			throw new BusinessException(AGENDA_CREATE_FAILED);
		}

		agenda.updateInformation(agendaDto.getAgendaTitle(), agendaDto.getAgendaContent());
		agenda.updateIsOfficial(agendaDto.getIsOfficial());
		agenda.updateIsRanking(agendaDto.getIsRanking());
		agenda.updateAgendaStatus(agendaDto.getAgendaStatus());
		agenda.updateSchedule(agendaDto.getAgendaDeadLine(), agendaDto.getAgendaStartTime(),
			agendaDto.getAgendaEndTime());
		agenda.updateLocation(agendaDto.getAgendaLocation(), teams);
		agenda.updateAgendaCapacity(agendaDto.getAgendaMinTeam(), agendaDto.getAgendaMaxTeam(), teams);
		agenda.updateAgendaTeamCapacity(agendaDto.getAgendaMinPeople(), agendaDto.getAgendaMaxPeople(), teams);
	}

	@Transactional(readOnly = true)
	public List<AgendaAdminSimpleResDto> getAgendaSimpleList() {
		return agendaAdminRepository.findAll().stream()
			.map(AgendaAdminSimpleResDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Optional<Agenda> getAgenda(UUID agendaKey) {
		return agendaAdminRepository.findByAgendaKey(agendaKey);
	}
}
