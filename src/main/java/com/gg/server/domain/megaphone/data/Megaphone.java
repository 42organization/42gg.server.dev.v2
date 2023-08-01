package com.gg.server.domain.megaphone.data;

import com.gg.server.domain.user.data.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Megaphone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

  /*  @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;*/

    @Column(name = "content", length = 30)
    private String content;

    @NotNull
    @Column(name = "used_at")
    private LocalDate usedAt;

    //public Megaphone(User user, Receipt receipt, String content, LocalDate usedAt) {
    public Megaphone(User user, String content, LocalDate usedAt) {
        this.user = user;
        //this.receipt = receipt;
        this.content = content;
        this.usedAt = usedAt;
    }
}
