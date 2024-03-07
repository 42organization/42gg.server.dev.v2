package gg.party.api.admin.comment.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.admin.repo.comment.CommentAdminRepository;
import gg.data.party.Comment;
import gg.party.api.admin.comment.controller.request.CommentUpdateAdminReqDto;
import gg.utils.exception.party.CommentNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentAdminService {
	private final CommentAdminRepository commentAdminRepository;

	/**
	 * 댓글 숨김
	 * @param commentId 댓글 번호
	 * @exception CommentNotFoundException 유효하지 않은 댓글
	 */
	@Transactional
	public void modifyHideComment(Long commentId, CommentUpdateAdminReqDto reqDto) {
		Comment comment = commentAdminRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);
		comment.updateHidden(reqDto.getIsHidden());
	}
}
