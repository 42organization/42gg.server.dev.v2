package gg.pingpong.api.user.megaphone.controller.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MegaphoneUseRequestDto {
	@NotNull(message = "receiptId는 null이 될 수 없습니다.")
	private Long receiptId;
	@NotNull(message = "content는 null이 될 수 없습니다.")
	@Size(max = 30)
	private String content;
}
