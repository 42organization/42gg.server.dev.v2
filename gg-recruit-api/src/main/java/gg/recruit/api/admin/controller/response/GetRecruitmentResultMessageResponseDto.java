package gg.recruit.api.admin.controller.response;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class GetRecruitmentResultMessageResponseDto {
	private long messageId;
	private MessageType messageType;
	private Boolean isUse;
	private String message;

	@Mapper
	public interface MapStruct {
		MapStruct INSTANCE = Mappers.getMapper(MapStruct.class);

		@Mapping(source = "id", target = "messageId")
		@Mapping(source = "content", target = "message")
		GetRecruitmentResultMessageResponseDto entityToDto(ResultMessage dto);
	}

}
