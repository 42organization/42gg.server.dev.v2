package gg.pingpong.api.user.manage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.manage.Feedback;
import gg.data.user.User;
import gg.pingpong.api.user.manage.controller.request.FeedbackRequestDto;
import gg.repo.manage.FeedbackRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackRepository feedbackRepository;
	private final UserRepository userRepository;

	@Transactional
	public void addFeedback(FeedbackRequestDto feedbackRequestDto, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		Feedback feedback = new Feedback(user, feedbackRequestDto.getCategory(), feedbackRequestDto.getContent());
		feedbackRepository.save(feedback);
	}
}
