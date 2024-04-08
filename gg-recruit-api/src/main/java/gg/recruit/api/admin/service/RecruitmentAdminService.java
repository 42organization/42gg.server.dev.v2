package gg.recruit.api.admin.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.recruit.ApplicationAdminRepository;
import gg.admin.repo.recruit.RecruitmentAdminRepository;
import gg.admin.repo.recruit.recruitment.RecruitStatusAdminRepository;
import gg.data.recruit.application.Application;
import gg.data.recruit.application.RecruitStatus;
import gg.data.recruit.application.enums.ApplicationStatus;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.admin.service.param.CheckListContent;
import gg.recruit.api.admin.service.param.FormParam;
import gg.recruit.api.admin.service.param.GetRecruitmentApplicationsParam;
import gg.recruit.api.admin.service.param.UpdateApplicationStatusParam;
import gg.recruit.api.admin.service.param.UpdateRecruitStatusParam;
import gg.recruit.api.admin.service.result.AllRecruitmentsResult;
import gg.recruit.api.admin.service.result.RecruitmentDetailAdminSvcDto;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.exception.recruitment.InvalidCheckListException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentAdminService {
	private final RecruitmentAdminRepository recruitmentAdminRepository;
	private final ApplicationAdminRepository applicationAdminRepository;
	private final EntityManager entityManager;
	private final RecruitStatusAdminRepository recruitStatusAdminRepository;

	/**
	 * 채용 공고를 생성한다.
	 * Recruitment, Question, CheckList을 저장한다.
	 * @param recruitment Recruitment
	 * @param forms Question, CheckList이 포함
	 * @return Recruitment 생성된 채용 공고
	 */
	@Transactional
	public Recruitment createRecruitment(Recruitment recruitment, List<FormParam> forms) {
		for (int i = 0; i < forms.size(); i++) {
			FormParam form = forms.get(i);
			Question question = form.toQuestion(recruitment, i + 1);
			InputType inputType = question.getInputType();

			if (inputType == InputType.SINGLE_CHECK || inputType == InputType.MULTI_CHECK) {
				addCheckList(question, form.getCheckList());
			}
		}
		return recruitmentAdminRepository.save(recruitment);
	}

	@Transactional
	public void updateRecruitStatus(UpdateRecruitStatusParam updateRecruitStatusParam) {
		Recruitment recruitments = recruitmentAdminRepository.findById(updateRecruitStatusParam.getRecruitId())
			.orElseThrow(() -> new NotExistException("Recruitment not found."));
		recruitments.setFinish(updateRecruitStatusParam.getFinish());
	}

	/**
	 * 공고 종료 날짜 기준으로 내림차순(최근순) 정렬하여 채용 공고를 조회한다.
	 * @param pageable
	 * @return 조회된 채용 공고 리스트
	 */
	@Transactional(readOnly = true)
	public AllRecruitmentsResult getAllRecruitments(Pageable pageable) {
		Page<Recruitment> allByOrderByEndDateDesc = recruitmentAdminRepository.findAllByIsDeletedOrderByEndTimeDesc(
			false, pageable);
		return new AllRecruitmentsResult(allByOrderByEndDateDesc.getTotalPages(), allByOrderByEndDateDesc.getContent());
	}

	/**
	 * 공고 삭제
	 * @param recruitId 삭제할 공고 id
	 */
	@Transactional
	public void deleteRecruitment(Long recruitId) {
		Recruitment recruitment = recruitmentAdminRepository.findById(recruitId)
			.orElseThrow(() -> new NotExistException("Recruitment not found."));
		recruitment.del();
	}

	/**
	 * 채용 공고를 수정한다.
	 * @param recruitId 채용 공고 ID
	 * @param updatedRecruitment 수정할 채용 공고
	 * @param forms 수정할 질문과 선택지
	 * @throws NotExistException 채용 공고가 존재하지 않을 때 발생
	 * @throws IllegalArgumentException 채용 공고 시작 시간이 현재 시간과 같거나 이후일 때 발생
	 */
	@Transactional
	public Recruitment updateRecruitment(Long recruitId, Recruitment updatedRecruitment, List<FormParam> forms) {
		Recruitment target = recruitmentAdminRepository.findById(recruitId)
			.orElseThrow(() -> new NotExistException("공고를 찾을 수 없습니다."));
		LocalDateTime now = LocalDateTime.now();
		if (target.getIsFinish() || target.getStartTime().isEqual(now) || target.getStartTime().isBefore(now)) {
			throw new ForbiddenException("수정 불가능한 공고입니다.");
		}
		target.update(updatedRecruitment);
		return target;
	}

	/**
	 * @param question 질문
	 * @param checkLists 선택지
	 * @throws InvalidCheckListException 선택지가 필요한데 비어있을 때 발생
	 */
	private void addCheckList(Question question, List<CheckListContent> checkLists) {
		if (checkLists == null || checkLists.isEmpty()) {
			throw new InvalidCheckListException();
		}
		for (CheckListContent checkList : checkLists) {
			new CheckList(question, checkList.getContent());
		}
	}

	/**
	 * 최종 결과 등록 후 알림
	 * @param dto
	 */
	@Transactional
	public void updateFinalApplicationStatusAndNotification(UpdateApplicationStatusParam dto) {
		Application application = applicationAdminRepository
			.findByIdAndRecruitId(dto.getApplicationId(), dto.getRecruitId())
			.orElseThrow(() -> new NotExistException("Application not found."));
		finalApplicationStatusCheck(dto.getStatus());
		updateApplicationStatus(dto);
		// TODO 사이클로 SNS Noti 접근이 불가능해 알림 기능 추후 구현
	}

	/**
	 * 최종 결과에 해당하는 값인지 검증
	 * @param status
	 */
	private void finalApplicationStatusCheck(ApplicationStatus status) {
		if (!status.isFinal) {
			throw new BusinessException(ErrorCode.BAD_ARGU);
		}
	}

	/**
	 * 지원서 상태 변경
	 * @param dto
	 */
	@Transactional
	public void updateApplicationStatus(UpdateApplicationStatusParam dto) {
		Application application = applicationAdminRepository
			.findByIdAndRecruitId(dto.getApplicationId(), dto.getRecruitId())
			.orElseThrow(() -> new NotExistException("Application not found."));
		application.updateApplicationStatus(dto.getStatus());
	}

	/**
	 * 서류 결과 등록
	 * 합격일 경우, 면접 일자 등록 및 변경 후 알림 전송
	 * @param dto UpdateApplicationStatusDto 서류 결과 등록 정보
	 * @throws NotExistException 지원서 또는 채용 공고가 존재하지 않을 때
	 * @throws ForbiddenException 서류전형 진행중인 지원서가 아닐 때
	 *
	 */
	@Transactional
	public void updateDocumentScreening(UpdateApplicationStatusParam dto) {
		Application application = applicationAdminRepository
			.findByIdAndRecruitId(dto.getApplicationId(), dto.getRecruitId())
			.orElseThrow(() -> new NotExistException("Application not found."));
		if (application.getStatus() != ApplicationStatus.PROGRESS_DOCS) {
			throw new ForbiddenException("서류전형 진행중인 지원서만 면접 일자를 등록할 수 있습니다.");
		}
		if (dto.getStatus() == ApplicationStatus.PROGRESS_INTERVIEW) {
			updateInterviewDate(application, dto.getRecruitId(), dto.getInterviewDate());
		}
		application.updateApplicationStatus(dto.getStatus());
		// TODO 사이클로 SNS Noti 접근이 불가능해 알림 기능 추후 구현
	}

	/**
	 * 면접 일자 등록 및 변경
	 * @param application 지원서
	 * @param recruitId 채용 공고 id
	 * @param interviewDate 면접 일자
	 * @throws DuplicationException 면접 시간이 중복될 때
	 */
	private void updateInterviewDate(Application application, long recruitId, LocalDateTime interviewDate) {
		int minutes = 30;
		boolean isDuplicated = recruitStatusAdminRepository.existsByRecruitmentIdAndInterviewDateBetween(
			recruitId, interviewDate.minusMinutes(minutes), interviewDate.plusMinutes(minutes));
		if (isDuplicated) {
			throw new DuplicationException("면접 시간이 중복됩니다.");
		}
		RecruitStatus recruitStatus = new RecruitStatus(application, interviewDate);
		recruitStatusAdminRepository.save(recruitStatus);
	}

	public List<Application> getRecruitmentApplicants(Long recruitId) {
		return applicationAdminRepository
			.findAllByRecruitmentIdWithUserAndRecruitStatusFetchJoinOrderByIdDesc(recruitId);
	}

	public Page<Application> findApplicationsWithAnswersAndUserWithFilter(GetRecruitmentApplicationsParam dto) {
		Long recruitId = dto.getRecruitId();
		Long questionId = dto.getQuestionId();
		String search = dto.getSearch();
		Pageable pageable = dto.getPageable();
		List<Long> checkListIds = dto.getCheckListIds();

		if (questionId != null && !checkListIds.isEmpty() && search == null) {
			return applicationAdminRepository.findAllByCheckList(recruitId, questionId, checkListIds, pageable);
		} else if (questionId != null && search != null && checkListIds.isEmpty()) {
			return applicationAdminRepository.findAllByContainSearch(recruitId, questionId, search, pageable);
		} else {
			return applicationAdminRepository.findByRecruitIdAndIsDeletedFalseOrderByIdDesc(recruitId, pageable);
		}
	}

	public RecruitmentDetailAdminSvcDto findRecruitmentDetail(Long recruitId) {
		Recruitment recruitment = recruitmentAdminRepository.findNotDeletedRecruit(recruitId)
			.orElseThrow(() -> new NotExistException("Recruitment not found."));
		return new RecruitmentDetailAdminSvcDto(recruitment);
	}
}
