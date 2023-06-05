package com.gg.server.admin.feedback.controller;

import com.gg.server.admin.feedback.dto.FeedbackAdminPageRequestDto;
import com.gg.server.admin.feedback.dto.FeedbackListAdminResponseDto;
import com.gg.server.admin.feedback.service.FeedbackAdminService;
import com.gg.server.global.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("pingpong/admin/feedback")
@RequiredArgsConstructor
@Validated
public class FeedbackAdminController {
    private final FeedbackAdminService feedbackAdminService;

    @GetMapping
    public FeedbackListAdminResponseDto feedbackAll(@ModelAttribute @Valid PageRequestDto req) {

        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), Sort.by("isSolved").and(Sort.by("createdAt")));
        return feedbackAdminService.findAllFeedback(pageable);
    }

    @PatchMapping("/{id}")
    public ResponseEntity feedbackIsSolvedToggle(@PathVariable @NotNull Long id){
        feedbackAdminService.toggleFeedbackIsSolvedByAdmin(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public FeedbackListAdminResponseDto feedbackFindByIntraId(@ModelAttribute @Valid FeedbackAdminPageRequestDto req) {

        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), Sort.by("intra_id").and(Sort.by("createdAt")));

        return feedbackAdminService.findByPartsOfIntraId(req.getIntraId(), pageable);
    }
}
