package gg.recruit.api.admin.service.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * RecruitmentResultMessageDto.
 *
 * <p>
 *
 * </p>
 * @see             :
 * @author          : middlefitting
 * @since           : 2024/03/20
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentResultMessageDto {
	@NotNull
	private MessageType messageType;

	@NotEmpty
	@Size(max = ResultMessage.contentLimit)
	private String message;
}
