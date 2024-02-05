package com.gg.server.domain.feedback.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.data.manage.Feedback;
import com.gg.server.data.user.User;
import com.gg.server.domain.feedback.data.FeedbackRepository;
import com.gg.server.domain.feedback.dto.FeedbackRequestDto;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;

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
