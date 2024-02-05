package com.gg.server.admin.noti.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendNotiAdminRequestDto {

	@NotNull
	private String intraId;

	@NotNull
	@Size(max = 255)
	private String message;

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SendNotiAdminRequestDto)) {
			return false;
		}
		SendNotiAdminRequestDto other = (SendNotiAdminRequestDto)obj;
		return Objects.equals(message, other.message);
	}
}
