package com.gg.server.domain.noti;

import com.gg.server.domain.user.User;
import com.gg.server.global.types.NotiType;
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
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @NotNull
    @Column(name = "noti_type")
    @Enumerated(EnumType.STRING)
    private NotiType type;

    @Column(name = "message")
    private String message;

    @Setter
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
}
