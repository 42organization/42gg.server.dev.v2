package com.gg.server.domain.user.data;

import com.gg.server.admin.user.dto.UserUpdateAdminRequestDto;
import com.gg.server.domain.user.type.*;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "intra_id", length = 30)
    private String intraId;

    @Column(name = "e_mail", length = 60)
    private String eMail;

    @Column(name = "image_uri")
    private String imageUri;

    @Enumerated(EnumType.STRING)
    @Column(name = "racket_type", length = 10)
    private RacketType racketType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 10)
    private RoleType roleType;

    @Column(name = "total_exp")
    private Integer totalExp;

    @Column(name = "sns_noti_opt", length = 10)
    @Enumerated(EnumType.STRING)
    private SnsType snsNotiOpt;

    @Column(name = "kakao_id")
    private Long kakaoId;

    @Column(name = "gg_coin")
    private Integer ggCoin;

    @Column(name = "background")
    @Enumerated(EnumType.STRING)
    private BackgroundType background;

    @Column(name = "text_color", length = 10)
    private String textColor;

    @Column(name = "edge")
    @Enumerated(EnumType.STRING)
    private EdgeType edge;

    @Builder
    public User(String intraId, String eMail, String imageUri, RacketType racketType,
                RoleType roleType, Integer totalExp, SnsType snsNotiOpt, Long kakaoId) {
        this.intraId = intraId;
        this.eMail = eMail;
        this.imageUri = imageUri;
        this.racketType = racketType;
        this.roleType = roleType;
        this.totalExp = totalExp;
        this.snsNotiOpt = snsNotiOpt;
        this.kakaoId = kakaoId;
        this.background = BackgroundType.BASIC;
        this.textColor = "#000000";
        this.edge = EdgeType.BASIC;
        this.ggCoin = 0;
    }

    public void modifyUserDetail(UserUpdateAdminRequestDto updateReq) {
        this.eMail = updateReq.getEmail();
        this.racketType = updateReq.getRacketType();
        this.roleType = RoleType.of(updateReq.getRoleType());
    }

    public void imageUpdate(String imageUri) {
        this.imageUri = imageUri;
    }

    public void updateTypes(RacketType racketType, SnsType snsType) {
        this.racketType = racketType;
        this.snsNotiOpt = snsType;
    }

    public void addExp(int plus) {
        this.totalExp += plus;
    }

    public void updateExp(int beforeExp) {
        this.totalExp = beforeExp;
    }

    public void updateKakaoId(Long kakaoId) {
        this.kakaoId = kakaoId;
    }
}