package gg.data.recruit.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import gg.data.BaseTimeEntity;
import gg.data.recruit.application.enums.ApplicationStatus;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.user.User;
import gg.utils.exception.BusinessChecker;
import gg.utils.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Application extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recruit_id", nullable = false)
	private Recruitment recruit;

	@OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
	private RecruitStatus recruitStatus;

	private Boolean isDeleted;

	@Enumerated(EnumType.STRING)
	@Column(length = 15, nullable = false)
	private ApplicationStatus status;

	public Application(User user, Recruitment recruit) {
		this.user = user;
		this.recruit = recruit;
		this.isDeleted = false;
		this.status = ApplicationStatus.PROGRESS_DOCS;
	}

	public String getRecruitTitle() {
		return this.recruit.getTitle();
	}

	public void delete() {
		this.isDeleted = Boolean.TRUE;
	}

	public Boolean isUpdateAvailable() {
		return !recruit.isEnd();
	}

	public void updateApplicationStatus(ApplicationStatus status) {
		BusinessChecker.mustNotNull(status, ErrorCode.BAD_ARGU);
		this.status = status;
	}
}
