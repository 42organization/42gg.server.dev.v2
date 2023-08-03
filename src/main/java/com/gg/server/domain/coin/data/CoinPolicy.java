package com.gg.server.domain.coin.data;

import com.gg.server.admin.coin.dto.CoinPolicyAdminAddDto;
import com.gg.server.domain.user.data.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class CoinPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "attendance")
    private int attendance;

    @Column(name = "normal")
    private int normal;

    @Column(name = "rankWin")
    private int rankWin;

    @Column(name = "rankLose")
    private int rankLose;

    @CreatedDate
    @Column(name = "createdAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public CoinPolicy(User user, int attendance, int normal, int rankWin, int rankLose) {
        this.user = user;
        this.attendance = attendance;
        this.normal = normal;
        this.rankWin = rankWin;
        this.rankLose = rankLose;
        this.createdAt = LocalDateTime.now();
    }

    static public CoinPolicy from(User user, CoinPolicyAdminAddDto addDto) {
        return CoinPolicy.builder()
                .user(user)
                .attendance(addDto.getAttendance())
                .normal(addDto.getNormal())
                .rankWin(addDto.getRankWin())
                .rankLose(addDto.getRankLose())
                .build();
    }
}