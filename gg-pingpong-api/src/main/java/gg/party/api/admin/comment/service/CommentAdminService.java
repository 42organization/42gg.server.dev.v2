package gg.party.api.admin.comment.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.admin.repo.comment.CommentAdminRepository;
import gg.data.party.Comment;
import gg.party.api.admin.comment.controller.request.CommentUpdateAdminRequestDto;
import gg.utils.exception.party.CommentNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentAdminService {
	private final CommentAdminRepository commentAdminRepository;

	@Transactional
	public void hideComment(Long commentId, CommentUpdateAdminRequestDto reqDto) {
		Comment comment = commentAdminRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);
		comment.updateHidden(reqDto.getIsHidden());
	}
}
