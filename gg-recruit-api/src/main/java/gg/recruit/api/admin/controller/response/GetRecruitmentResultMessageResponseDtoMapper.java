package gg.recruit.api.admin.controller.response;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import gg.data.recruit.manage.ResultMessage;

@Mapper
public interface GetRecruitmentResultMessageResponseDtoMapper {
	GetRecruitmentResultMessageResponseDtoMapper INSTANCE = Mappers.getMapper(
		GetRecruitmentResultMessageResponseDtoMapper.class);

	@Mapping(source = "id", target = "messageId")
	@Mapping(source = "content", target = "message")
	GetRecruitmentResultMessageResponseDto entityToDto(ResultMessage dto);
}
