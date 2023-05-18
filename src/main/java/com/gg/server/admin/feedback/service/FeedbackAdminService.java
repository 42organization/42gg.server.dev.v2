package com.gg.server.admin.feedback.service;

import com.gg.server.admin.feedback.data.FeedbackAdminRepository;
import com.gg.server.admin.feedback.dto.FeedbackAdminResponseDto;
import com.gg.server.admin.feedback.dto.FeedbackListAdminResponseDto;
import com.gg.server.domain.feedback.data.Feedback;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackAdminService {
    private final FeedbackAdminRepository feedbackAdminRepository;

    @Transactional(readOnly = true)
    public FeedbackListAdminResponseDto findAllFeedback(Pageable pageable){
        Page<Feedback> feedbacks = feedbackAdminRepository.findAll(pageable);
        Page<FeedbackAdminResponseDto> feedbackAdminResponseDtos = feedbacks.map(FeedbackAdminResponseDto::new);

        FeedbackListAdminResponseDto responseDto = new FeedbackListAdminResponseDto(feedbackAdminResponseDtos.getContent(),
                feedbackAdminResponseDtos.getTotalPages(), feedbackAdminResponseDtos.getNumber() + 1);
        return responseDto;
    }

}
