package com.gg.server.domain.feedback.service;

import com.gg.server.domain.feedback.data.Feedback;
import com.gg.server.domain.feedback.data.FeedbackRepository;
import com.gg.server.domain.feedback.dto.FeedbackRequestDto;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addFeedback(FeedbackRequestDto feedbackRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User" + userId));

        Feedback feedback = new Feedback(user, feedbackRequestDto.getCategory(), feedbackRequestDto.getContent());
        feedbackRepository.save(feedback);
    }
}
