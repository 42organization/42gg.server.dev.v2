package gg.party.api.admin.comment.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.admin.repo.comment.CommentAdminRepository;
import gg.data.party.Comment;
import gg.party.api.admin.comment.controller.request.CommentUpdateAdminReqDto;
import gg.utils.exception.party.ChangeSameStatusException;
import gg.utils.exception.party.CommentNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentAdminService {
	private final CommentAdminRepository commentAdminRepository;

	/**
	 * 댓글 숨김
	 * @param commentId 댓글 번호
	 * @throws CommentNotFoundException 유효하지 않은 댓글 입력 - 404
	 * @throws ChangeSameStatusException 같은 상태로 변경 - 409
	 */
	@Transactional
	public void modifyHideComment(Long commentId, CommentUpdateAdminReqDto reqDto) {
		Comment comment = commentAdminRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);
		if (reqDto.getIsHidden() == comment.isHidden()) {
			throw new ChangeSameStatusException();
		}
		comment.updateHidden(reqDto.getIsHidden());
	}
}
