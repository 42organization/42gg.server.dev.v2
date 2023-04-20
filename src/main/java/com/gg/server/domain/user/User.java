package com.gg.server.domain.user;

import com.gg.server.global.types.user.RacketType;
import com.gg.server.global.types.user.RoleType;
import com.gg.server.global.types.user.SnsType;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Setter
    @Column(name = "intra_id")
    private String intraId;

    @Setter
    @Column(name = "e_mail")
    private String eMail;

    @Setter
    @Column(name = "image_uri")
    private String imageUri;

    @Setter
    @Column(name = "racket_type")
    private RacketType racketType;

    @Setter
    @NotNull
    @Column(name = "status_message")
    private String statusMessage;

    @Setter
    @NotNull
    @Column(name = "role_type")
    private RoleType roleType;

    @Setter
    //    @NotNull
    @Column(name = "total_exp")
    private Integer totalExp;

    @Setter
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
}
