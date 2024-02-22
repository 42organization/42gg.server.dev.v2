package gg.pingpong.api.user.feedback.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.user.feedback.controller.request.FeedbackRequestDto;
import gg.pingpong.data.manage.Feedback;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.feedback.FeedbackRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.exception.user.UserNotFoundException;
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
