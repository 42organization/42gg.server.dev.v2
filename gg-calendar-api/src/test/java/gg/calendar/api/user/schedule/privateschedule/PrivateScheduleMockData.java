package gg.calendar.api.user.schedule.privateschedule;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.repo.calendar.PublicScheduleRepository;
import gg.repo.calendar.ScheduleGroupRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrivateScheduleMockData {
	private final PublicScheduleRepository publicScheduleRepository;
	private final ScheduleGroupRepository scheduleGroupRepository;
	private final PrivateScheduleRepository privateScheduleRepository;

	public PublicSchedule createPublicSchedule(String author, DetailClassification classification) {
		PublicSchedule publicSchedule = PublicSchedule.builder()
			.classification(classification)
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

	public PublicSchedule createPublicScheduleDeactivate(String author, DetailClassification classification) {
		PublicSchedule publicSchedule = PublicSchedule.builder()
			.classification(classification)
			.eventTag(null)
			.jobTag(null)
			.techTag(null)
			.title("Test Schedule")
			.author(author)
			.content("Test Content")
			.link("http://test.com")
			.status(ScheduleStatus.DEACTIVATE)
			.startTime(LocalDateTime.now().minusDays(3))
			.endTime(LocalDateTime.now().minusDays(1))
			.build();
		return publicScheduleRepository.save(publicSchedule);
	}

	public ScheduleGroup createScheduleGroup(User user) {
		ScheduleGroup scheduleGroup = ScheduleGroup.builder()
			.user(user)
			.title("title")
			.backgroundColor("#FFFFFF")
			.build();
		return scheduleGroupRepository.save(scheduleGroup);
	}

	public PrivateSchedule createPrivateSchedule(User user, PublicSchedule publicSchedule, Long scheduleGroupId) {
		PrivateSchedule privateSchedule = new PrivateSchedule(user, publicSchedule, false, scheduleGroupId,
			ScheduleStatus.ACTIVATE);
		return privateScheduleRepository.save(privateSchedule);
	}

	public PrivateSchedule createPrivateScheduleDeactivate(User user, PublicSchedule publicSchedule,
		Long scheduleGroupId) {
		PrivateSchedule privateSchedule = new PrivateSchedule(user, publicSchedule, false, scheduleGroupId,
			ScheduleStatus.DEACTIVATE);
		return privateScheduleRepository.save(privateSchedule);
	}
}
