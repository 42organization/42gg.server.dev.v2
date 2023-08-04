package com.gg.server.domain.penalty.data;

import com.gg.server.domain.penalty.type.PenaltyType;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Penalty extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "penalty_type", length = 20)
    @Enumerated(EnumType.STRING)
    private PenaltyType type;

    @Column(name = "message", length = 100)
    private String message;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "penalty_time")
    private Integer penaltyTime;

    public Penalty(User user, PenaltyType type, String message, LocalDateTime startTime, Integer penaltyTime) {
        this.user = user;
        this.type = type;
        this.message = message;
        this.startTime = startTime;
        this.penaltyTime = penaltyTime;
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
