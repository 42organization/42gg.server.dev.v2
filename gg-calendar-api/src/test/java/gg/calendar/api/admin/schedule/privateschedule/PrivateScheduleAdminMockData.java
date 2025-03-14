package gg.calendar.api.admin.schedule.privateschedule;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import gg.admin.repo.calendar.PrivateScheduleAdminRepository;
import gg.admin.repo.calendar.PublicScheduleAdminRepository;
import gg.admin.repo.calendar.ScheduleGroupAdminRepository;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrivateScheduleAdminMockData {

	private final PublicScheduleAdminRepository publicScheduleRepository;
	private final ScheduleGroupAdminRepository scheduleGroupRepository;
	private final PrivateScheduleAdminRepository privateScheduleRepository;

	public PublicSchedule createPublicSchedule(String author) {
		PublicSchedule publicSchedule = PublicSchedule.builder()
			.classification(DetailClassification.EVENT)
			.eventTag(null)
			.jobTag(null)
			.techTag(null)
			.title("Test Schedule")
			.author(author)
			.content("Test Content")
			.link("http://test.com")
			.status(ScheduleStatus.ACTIVATE)
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusDays(1))
			.build();
		return publicScheduleRepository.save(publicSchedule);
	}

	public PublicSchedule createPublicPrivateSchedule(String author) {
		PublicSchedule publicSchedule = PublicSchedule.builder()
			.classification(DetailClassification.PRIVATE_SCHEDULE)
			.eventTag(null)
			.jobTag(null)
			.techTag(null)
			.title("Test Schedule")
			.author(author)
			.content("Test Content")
			.link("http://test.com")
			.status(ScheduleStatus.ACTIVATE)
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusDays(1))
			.build();
		return publicScheduleRepository.save(publicSchedule);
	}

	public ScheduleGroup createScheduleGroup(User user) {
		ScheduleGroup scheduleGroup = ScheduleGroup.builder()
			.user(user)
			.title("title")
			.backgroundColor("")
			.build();
		return scheduleGroupRepository.save(scheduleGroup);
	}

	public PrivateSchedule createPrivateSchedule(PublicSchedule publicSchedule, ScheduleGroup scheduleGroup) {
		PrivateSchedule privateSchedule = new PrivateSchedule(scheduleGroup.getUser(), publicSchedule, false,
			scheduleGroup.getId(), ScheduleStatus.ACTIVATE);
		return privateScheduleRepository.save(privateSchedule);
	}

	public PrivateSchedule createPrivateScheduleNoGroup(PublicSchedule publicSchedule, User user) {
		PrivateSchedule privateSchedule = new PrivateSchedule(user, publicSchedule, false,
			500L, ScheduleStatus.ACTIVATE);
		return privateScheduleRepository.save(privateSchedule);
	}

	public void createPrivateSchedules(int size, User user) {
		for (int i = 0; i < size; i++) {
			PublicSchedule publicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.PRIVATE_SCHEDULE)
				.author("42GG")
				.title("Private " + i)
				.content("urgent")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now().plusDays(i))
				.endTime(LocalDateTime.now().plusDays(i + 1))
				.build();
			publicScheduleRepository.save(publicSchedule);

			ScheduleGroup scheduleGroup = ScheduleGroup.builder()
				.user(user)
				.title("title " + i)
				.backgroundColor("color " + i)
				.build();
			scheduleGroupRepository.save(scheduleGroup);

			PrivateSchedule privateSchedule = new PrivateSchedule(user, publicSchedule, false,
				scheduleGroup.getId(), ScheduleStatus.ACTIVATE);
		}
	}
}
