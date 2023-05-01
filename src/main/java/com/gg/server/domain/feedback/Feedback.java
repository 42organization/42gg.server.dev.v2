package com.gg.server.domain.feedback;

import com.gg.server.domain.user.User;
import com.gg.server.global.types.FeedbackType;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Feedback extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @NotNull
    @Column(name = "category")
    private FeedbackType category;

    @NotNull
    @Column(name = "content", length = 600)
    private String content;

    @Setter
    @NotNull
    @Column(name = "is_solved")
    private Boolean isSolved;
}
