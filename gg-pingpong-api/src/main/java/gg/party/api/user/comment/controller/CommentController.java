package gg.party.api.user.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.party.api.user.comment.controller.request.CommentCreateReqDto;
import gg.party.api.user.comment.service.CommentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/rooms/{roomId}/comments")
public class CommentController {

	private final CommentService commentService;

	/**
	 * 댓글 생성
	 * @param reqDto 댓글 정보
	 * @param roomId 방 번호
	 * @return 생성 성공 여부
	 */
	@PostMapping
	public ResponseEntity<Void> createComment(@PathVariable Long roomId, @RequestBody CommentCreateReqDto reqDto,
		@Login UserDto user) {
		commentService.createComment(roomId, reqDto, user.getId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
