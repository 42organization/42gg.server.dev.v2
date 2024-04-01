package gg.recruit.api.admin.service.dto;

import gg.data.recruit.manage.ResultMessage;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T03:03:56+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.16.1 (Oracle Corporation)"
)
public class RecruitmentResultMessageDtoMapperImpl implements RecruitmentResultMessageDtoMapper {

    @Override
    public ResultMessage dtoToEntity(RecruitmentResultMessageDto dto) {
        if ( dto == null ) {
            return null;
        }

        ResultMessage.ResultMessageBuilder resultMessage = ResultMessage.builder();

        resultMessage.content( dto.getContent() );
        resultMessage.messageType( dto.getMessageType() );

        return resultMessage.build();
    }
}
