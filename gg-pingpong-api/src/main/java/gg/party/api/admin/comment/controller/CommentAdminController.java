package gg.party.api.admin.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.comment.controller.request.CommentUpdateAdminRequestDto;
import gg.party.api.admin.comment.service.CommentAdminService;
import gg.pingpong.api.global.utils.argumentresolver.Login;
import gg.pingpong.api.user.user.dto.UserDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/comments")
public class CommentAdminController {
	private final CommentAdminService commentAdminService;

	/**
	 * 댓글 삭제
	 * @param commentId 댓글 번호
	 * @return 삭제 성공 여부
	 */
	@PatchMapping("/{commentId}")
	public ResponseEntity<Void> hideComment(@PathVariable Long commentId,
		@RequestBody CommentUpdateAdminRequestDto reqDto, @Login UserDto user) {
		commentAdminService.hideComment(commentId, reqDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
