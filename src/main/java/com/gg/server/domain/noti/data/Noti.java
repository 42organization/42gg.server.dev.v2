package com.gg.server.domain.noti.data;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Noti extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "noti_type", length = 15)
    @Enumerated(EnumType.STRING)
    private NotiType type;

    @Column(name = "message", length = 255)
    private String message;

    @NotNull
    @Column(name = "is_checked")
    private Boolean isChecked;

    public Noti(User user, NotiType type, String message, Boolean isChecked) {
        this.user = user;
        this.type = type;
        this.message = message;
        this.isChecked = isChecked;
    }

    public void update(User user, NotiType type, String message, Boolean isChecked) {
        this.user = user;
        this.type = type;
        this.message = message;
        this.isChecked = isChecked;
    }

    public void modifyIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }
}
