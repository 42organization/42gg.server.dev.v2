package com.gg.server.admin.feedback.controller;

import com.gg.server.admin.feedback.dto.FeedbackListAdminResponseDto;
import com.gg.server.admin.feedback.service.FeedbackAdminService;
import com.gg.server.domain.feedback.data.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("pingpong/admin/feedback")
@RequiredArgsConstructor
@Validated
public class FeedbackAdminController {
    private final FeedbackAdminService feedbackAdminService;

    @GetMapping
    public FeedbackListAdminResponseDto feedbackAll(
            @RequestParam(defaultValue = "1") @Min(1) int page, @RequestParam(defaultValue = "5") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("isSolved").and(Sort.by("createdAt")));
        return feedbackAdminService.findAllFeedback(pageable);
    }
}
