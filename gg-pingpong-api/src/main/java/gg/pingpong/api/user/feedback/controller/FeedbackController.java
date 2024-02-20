package gg.pingpong.api.user.feedback.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.domain.feedback.dto.FeedbackRequestDto;
import com.gg.server.domain.feedback.service.FeedbackService;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pingpong")
public class FeedbackController {
	private final FeedbackService feedbackService;
	private final UserRepository userRepository;

	@PostMapping(value = "/feedback")
	public ResponseEntity feedbackSave(@Valid @RequestBody FeedbackRequestDto feedbackRequestDto,
		@Parameter(hidden = true) @Login UserDto user) {
		feedbackService.addFeedback(feedbackRequestDto, user.getId());
		return new ResponseEntity(HttpStatus.CREATED);
	}
}
