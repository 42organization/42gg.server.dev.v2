package com.gg.server.user;

import com.gg.server.user.type.RacketType;
import com.gg.server.user.type.RoleType;
import com.gg.server.user.type.SnsType;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "intra_id")
    private String intraId;

    @Column(name = "e_mail")
    private String eMail;

    @Column(name = "image_uri")
    private String imageUri;

    @Enumerated(EnumType.STRING)
    @Column(name = "racket_type")
    private RacketType racketType;

    @NotNull
    @Column(name = "status_message")
    private String statusMessage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    @Column(name = "total_exp")
    private Integer totalExp;

    @Column(name = "sns_noti_opt")
    @Enumerated(EnumType.STRING)
    private SnsType snsNotiOpt;

    @Builder
    public User(String intraId, String eMail, String imageUri, RacketType racketType,
                String statusMessage, RoleType roleType, Integer totalExp, SnsType snsNotiOpt) {
        this.intraId = intraId;
        this.eMail = eMail;
        this.imageUri = imageUri;
        this.racketType = racketType;
        this.statusMessage = statusMessage;
        this.roleType = roleType;
        this.totalExp = totalExp;
        this.snsNotiOpt = snsNotiOpt;
    }

    public void imageUpdate(String imageUri) {
        this.imageUri = imageUri;
    }
}
