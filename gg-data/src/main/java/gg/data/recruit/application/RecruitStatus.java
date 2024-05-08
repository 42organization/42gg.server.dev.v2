package gg.data.recruit.application;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import gg.data.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecruitStatus extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", nullable = false)
	private Application application;

	@Getter
	private LocalDateTime interviewDate;

	public RecruitStatus(Application application) {
		this.application = application;
	}

	public RecruitStatus(Application application, LocalDateTime interviewDate) {
		this.application = application;
		this.interviewDate = interviewDate;
	}

	public void updateInterviewDate(LocalDateTime interviewDate) {
		this.interviewDate = interviewDate;
	}
}
