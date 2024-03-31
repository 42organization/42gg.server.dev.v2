package gg.recruit.api.admin.service.dto;

import gg.data.recruit.manage.ResultMessage;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-31T20:09:45+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
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
