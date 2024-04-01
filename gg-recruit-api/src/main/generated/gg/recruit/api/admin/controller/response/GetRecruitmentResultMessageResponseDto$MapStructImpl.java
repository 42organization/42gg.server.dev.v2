package gg.recruit.api.admin.controller.response;

import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T03:03:56+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.16.1 (Oracle Corporation)"
)
public class GetRecruitmentResultMessageResponseDto$MapStructImpl implements GetRecruitmentResultMessageResponseDto.MapStruct {

    @Override
    public GetRecruitmentResultMessageResponseDto entityToDto(ResultMessage dto) {
        if ( dto == null ) {
            return null;
        }

        long messageId = 0L;
        String message = null;
        MessageType messageType = null;
        Boolean isUse = null;

        if ( dto.getId() != null ) {
            messageId = dto.getId();
        }
        message = dto.getContent();
        messageType = dto.getMessageType();
        isUse = dto.getIsUse();

        GetRecruitmentResultMessageResponseDto getRecruitmentResultMessageResponseDto = new GetRecruitmentResultMessageResponseDto( messageId, messageType, isUse, message );

        return getRecruitmentResultMessageResponseDto;
    }
}
