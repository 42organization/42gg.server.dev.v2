package gg.admin.repo.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Comment;

public interface CommentAdminRepository extends JpaRepository<Comment, Long> {
}
