package gg.pingpong.api.user.user.controller.response;

import gg.pingpong.data.user.type.EdgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserNormalDetailResponseDto {
	private String intraId;
	private String userImageUri;
	private Boolean isAdmin;
	private Boolean isAttended;
	private EdgeType edgeType;
	private String tierName;
	private String tierImageUri;
	private Integer level;
}
