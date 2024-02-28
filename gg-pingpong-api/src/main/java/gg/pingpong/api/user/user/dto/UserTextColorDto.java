package gg.pingpong.api.user.user.dto;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserTextColorDto {
	@NotNull
	private Long receiptId;
	@NotNull
	private String textColor;
}
