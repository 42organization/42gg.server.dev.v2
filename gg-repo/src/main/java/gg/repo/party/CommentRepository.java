package gg.repo.party;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
