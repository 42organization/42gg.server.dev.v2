package com.gg.server.admin.feedback.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.admin.feedback.dto.FeedbackAdminPageRequestDto;
import com.gg.server.admin.feedback.dto.FeedbackListAdminResponseDto;
import com.gg.server.admin.feedback.service.FeedbackAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("pingpong/admin/feedback")
@RequiredArgsConstructor
@Validated
public class FeedbackAdminController {
	private final FeedbackAdminService feedbackAdminService;

	@GetMapping
	public FeedbackListAdminResponseDto feedbackAll(@ModelAttribute @Valid FeedbackAdminPageRequestDto req) {

		if (req.getIntraId() == null) {
			Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(),
				Sort.by("isSolved").and(Sort.by("createdAt")));
			return feedbackAdminService.findAllFeedback(pageable);
		}
		Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(),
			Sort.by("intra_id").and(Sort.by("createdAt")));
		return feedbackAdminService.findByPartsOfIntraId(req.getIntraId(), pageable);
	}

	@PatchMapping("/{feedbackId}")
	public ResponseEntity feedbackIsSolvedToggle(@PathVariable @NotNull Long feedbackId) {
		feedbackAdminService.toggleFeedbackIsSolvedByAdmin(feedbackId);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
