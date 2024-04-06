package gg.recruit.api.admin.service.param;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gg.data.recruit.manage.ResultMessage;

@Mapper
public interface RecruitmentResultMessageDtoMapper {
	RecruitmentResultMessageDtoMapper INSTANCE = Mappers.getMapper(RecruitmentResultMessageDtoMapper.class);

	ResultMessage dtoToEntity(RecruitmentResultMessageParam dto);
}
