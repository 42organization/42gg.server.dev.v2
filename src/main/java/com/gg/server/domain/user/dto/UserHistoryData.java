package com.gg.server.domain.user.dto;

import java.time.LocalDateTime;

import com.gg.server.domain.pchange.data.PChange;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserHistoryData {
	private int ppp;
	private LocalDateTime date;

	public UserHistoryData(PChange pChange) {
		this.ppp = pChange.getPppResult();
		this.date = pChange.getCreatedAt();
	}
}
