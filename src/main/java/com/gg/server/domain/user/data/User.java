package com.gg.server.domain.user.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;

import com.gg.server.admin.user.dto.UserUpdateAdminRequestDto;
import com.gg.server.domain.item.exception.InsufficientGgcoinException;
import com.gg.server.domain.user.type.BackgroundType;
import com.gg.server.domain.user.type.EdgeType;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.utils.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
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
		this.ggCoin = updateReq.getCoin();
	}

	public void updateImageUri(String imageUri) {
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

	public void updateTextColor(String textColor) {
		this.textColor = textColor;
	}

	public void updateEdge(EdgeType edge) {
		this.edge = edge;
	}

	public int addGgCoin(int plus) {
		this.ggCoin += plus;
		return this.ggCoin;
	}

	public void payGgCoin(int amount) {
		if (this.ggCoin < amount) {
			throw new InsufficientGgcoinException();  // 사용자의 ggCoin이 필요한 금액보다 적을 경우 예외를 발생
		}
		this.ggCoin = this.ggCoin - amount;
	}

	public void updateBackground(BackgroundType background) {
		this.background = background;
	}
}
