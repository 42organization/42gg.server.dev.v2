package gg.calendar.api.admin.schedule.publicschedule;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;

import gg.admin.repo.calendar.PublicScheduleAdminRepository;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PublicScheduleAdminMockData {

	private final EntityManager em;
	private final PublicScheduleAdminRepository publicScheduleAdminRepository;

	public PublicSchedule createPublicSchedule() {
		PublicSchedule publicSchedule = PublicSchedule.builder()
			.classification(DetailClassification.EVENT)
			.eventTag(EventTag.JOB_FORUM)
			.author("42GG")
			.title("Job Forum")
			.content("Job Forum Test.")
			.link("https://gg.42seoul.kr")
			.status(ScheduleStatus.ACTIVATE)
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusDays(10))
			.build();

		return publicScheduleAdminRepository.save(publicSchedule);
	}

	public void createPublicScheduleEvent(int size) {
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.JOB_FORUM)
				.author("42GG")
				.title("Job " + i)
				.content("TEST JOB")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 10))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
	}

	public void createPublicScheduleJob(int size) {
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(JobTag.EXPERIENCED)
				.author("42GG")
				.title("Job " + i)
				.content("TEST JOB")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 10))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
	}

	public void createPublicSchedulePrivate(int size) {
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.PRIVATE_SCHEDULE)
				.author("42GG")
				.title("Private " + i)
				.content("TEST Private")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 10))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
	}

	public void cratePublicScheduleArgumentsEvent(int size, String author, String title) {
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.JOB_FORUM)
				.author(author)
				.title(title + " " + i)
				.content("TEST EVENT")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 1))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
	}

	public void cratePublicScheduleArgumentsEventTime(int size, String author, String title) {
		for (int i = -size; i < 0; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.JOB_FORUM)
				.author(author)
				.title(title + " " + i)
				.content("TEST EVENT")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().minusDays(Math.abs(i)))
				.endTime(LocalDateTime.now().minusDays(Math.abs(i) - 1L))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
		for (int i = -size; i < 0; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.JOB_FORUM)
				.author(author)
				.title(title + " " + i)
				.content("TEST EVENT")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().minusDays(Math.abs(i)))
				.endTime(LocalDateTime.now().minusDays(Math.abs(i) - 2L))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
		for (int i = -size; i < 0; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.JOB_FORUM)
				.author(author)
				.title(title + " " + i)
				.content("TEST EVENT")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().minusDays(Math.abs(i)))
				.endTime(LocalDateTime.now().minusDays(Math.abs(i)))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.JOB_FORUM)
				.author(author)
				.title(title + " " + i)
				.content("TEST EVENT")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.JOB_FORUM)
				.author(author)
				.title(title + " " + i)
				.content("TEST EVENT")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 1))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.JOB_FORUM)
				.author(author)
				.title(title + " " + i)
				.content("TEST EVENT")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 2))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}

	}

	public void cratePublicScheduleArgumentsJob(int size, String author, String content) {
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(JobTag.EXPERIENCED)
				.author(author)
				.title("TEST " + i)
				.content(content + " " + i)
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 1))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
	}

	public void cratePublicScheduleArgumentsPrivate(int size, String author, String title) {
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.PRIVATE_SCHEDULE)
				.author(author)
				.title(title + " " + i)
				.content("TEST Private")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 1))
				.build();
			publicScheduleAdminRepository.save(publicSchedule);
		}
	}
}
