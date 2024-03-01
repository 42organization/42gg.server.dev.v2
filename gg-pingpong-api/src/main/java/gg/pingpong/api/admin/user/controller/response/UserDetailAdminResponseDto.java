package gg.pingpong.api.admin.user.controller.response;

import gg.data.rank.redis.RankRedis;
import gg.data.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailAdminResponseDto {
	private Long userId;
	private String intraId;
	private String userImageUri;
	private String racketType;
	private String statusMessage;
	private Integer wins;
	private Integer losses;
	private Integer ppp;
	private String email;
	private String roleType;
	private Integer exp;
	private Integer coin;

	public UserDetailAdminResponseDto(User user, RankRedis rank) {
		this.userId = user.getId();
		this.intraId = user.getIntraId();
		this.userImageUri = user.getImageUri();
		this.racketType = user.getRacketType().getCode();
		this.statusMessage = rank.getStatusMessage();
		this.wins = rank.getWins();
		this.losses = rank.getLosses();
		this.ppp = rank.getPpp();
		this.email = user.getEMail();
		this.roleType = user.getRoleType().getKey();
		this.exp = user.getTotalExp();
		this.coin = user.getGgCoin();
	}

	public UserDetailAdminResponseDto(User user) {
		this.userId = user.getId();
		this.intraId = user.getIntraId();
		this.userImageUri = user.getImageUri();
		this.racketType = user.getRacketType().getCode();
		this.statusMessage = "";
		this.wins = 0;
		this.losses = 0;
		this.ppp = 0;
		this.email = user.getEMail() == null ? "" : user.getEMail();
		this.roleType = user.getRoleType().getKey();
		this.exp = user.getTotalExp();
		this.coin = user.getGgCoin();
	}

	@Override
	public String toString() {
		return "UserDetailResponseDto{"
			+ "intraId='" + intraId + '\''
			+ ", userImageUri='" + userImageUri
			+ '\''
			+ ", racketType='"
			+ racketType + '\''
			+ ", statusMessage='"
			+ statusMessage + '\''
			+ ", wins='" + wins.toString()
			+ '\'' + ", losses='" + losses.toString()
			+ '\'' + ", ppp='" + ppp.toString() + '\''
			+ ", email='" + email + '\'' + ", roleType='"
			+ roleType + '\''
			+ ", coin='" + coin
			+ '\''
			+ '}';
	}
}
