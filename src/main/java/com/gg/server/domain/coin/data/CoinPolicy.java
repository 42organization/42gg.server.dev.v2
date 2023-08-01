package com.gg.server.domain.coin.data;

import lombok.AllArgsConstructor;
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

    @Column(name = "user_id", length = 30)
    private String CreateUserId;

    @Column(name = "attendance")
    private Long attendance;

    @Column(name = "nomal")
    private Long nomal;

    @Column(name = "rankWin")
    private Long rankWin;

    @Column(name = "rankLose")
    private Long rankLose;

    @CreatedDate
    @Column(name = "createdAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}