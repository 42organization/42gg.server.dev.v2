package gg.pingpong.api.user.user.controller.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileImageRequestDto {
	@NotNull
	private Long receiptId;
}
