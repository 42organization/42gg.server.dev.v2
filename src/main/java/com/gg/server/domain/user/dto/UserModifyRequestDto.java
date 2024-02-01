package com.gg.server.domain.user.dto;

import com.gg.server.data.user.type.RacketType;
import com.gg.server.data.user.type.SnsType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserModifyRequestDto {

	private RacketType racketType;
	private String statusMessage;
	private SnsType snsNotiOpt;
}
