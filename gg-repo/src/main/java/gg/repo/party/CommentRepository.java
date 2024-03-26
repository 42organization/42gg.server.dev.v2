package gg.repo.party;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gg.data.party.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByRoomId(Long roomId);

	@Query("SELECT c FROM Comment c "
		+ "JOIN FETCH c.user "
		+ "JOIN FETCH c.userRoom "
		+ "JOIN FETCH c.room r "
		+ "WHERE r.id = :roomId")
	List<Comment> findAllWithCommentFetchJoin(@Param("roomId") Long roomId);
}
