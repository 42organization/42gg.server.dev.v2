package gg.pingpong.api.user.user.controller.response;

import gg.pingpong.api.global.utils.ExpLevelCalculator;
import gg.pingpong.data.rank.Tier;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.type.BackgroundType;
import gg.pingpong.data.user.type.EdgeType;
import gg.pingpong.data.user.type.SnsType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailResponseDto {
	private String intraId;
	private String userImageUri;
	private String racketType;
	private String statusMessage;
	private Integer level;
	private Integer currentExp;
	private Integer maxExp;
	private Double expRate;
	private SnsType snsNotiOpt;
	private BackgroundType background;
	private String textColor;
	private EdgeType edge;
	private String tierName;
	private String tierImageUri;

	public UserDetailResponseDto(User user, String statusMessage, Tier tier) {
		this.intraId = user.getIntraId();
		this.racketType = user.getRacketType().getCode();
		this.userImageUri = user.getImageUri();
		this.statusMessage = statusMessage;
		this.snsNotiOpt = user.getSnsNotiOpt();
		this.background = user.getBackground();
		this.textColor = user.getTextColor();
		this.edge = user.getEdge();
		this.tierName = tier.getName();
		this.tierImageUri = tier.getImageUri();
		calculateExpAndLevel(user);
	}

	private void calculateExpAndLevel(User user) {
		this.currentExp = ExpLevelCalculator.getCurrentLevelMyExp(user.getTotalExp());
		this.maxExp = ExpLevelCalculator.getLevelMaxExp(ExpLevelCalculator.getLevel(user.getTotalExp()));
		this.level = ExpLevelCalculator.getLevel(user.getTotalExp());
		this.expRate = (double)(currentExp * 10000 / maxExp) / 100;
	}

}
