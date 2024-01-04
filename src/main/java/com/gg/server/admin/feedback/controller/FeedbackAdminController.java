package com.gg.server.admin.feedback.controller;

import com.gg.server.admin.feedback.dto.FeedbackAdminPageRequestDto;
import com.gg.server.admin.feedback.dto.FeedbackListAdminResponseDto;
import com.gg.server.admin.feedback.service.FeedbackAdminService;
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

    /**
     * <p>intraId 가 없다면 전체, 있다면 타겟 유저의 피드백들을 반환해줍니다.</p>
     * @param req
     * @return FeedbackListAdminResponseDto
     */
    @GetMapping
    public ResponseEntity<FeedbackListAdminResponseDto> feedbackAll(@ModelAttribute @Valid FeedbackAdminPageRequestDto req) {
        if (req.getIntraId() == null){
            Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), Sort.by("isSolved").and(Sort.by("createdAt")));
            return ResponseEntity.status(HttpStatus.OK).body(feedbackAdminService.findAllFeedback(pageable));
        }
        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), Sort.by("isSolved").and(Sort.by("createdAt")));
        return ResponseEntity.status(HttpStatus.OK).body(feedbackAdminService.findByPartsOfIntraId(req.getIntraId(), pageable));
    }

    /**
     * <p>타겟 피드백의 처리 상태를 변경해줍니다.</p>
     * @param feedbackId 타겟 피드백 id
     */
    @PatchMapping("/{feedbackId}")
    public ResponseEntity<Void> feedbackIsSolvedToggle(@PathVariable @NotNull Long feedbackId){
        feedbackAdminService.toggleFeedbackIsSolvedByAdmin(feedbackId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
