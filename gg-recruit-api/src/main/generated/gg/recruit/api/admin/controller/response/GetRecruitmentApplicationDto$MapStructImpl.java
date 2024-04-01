package gg.recruit.api.admin.controller.response;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.enums.ApplicationStatus;
import gg.data.user.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T03:22:03+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.16.1 (Oracle Corporation)"
)
public class GetRecruitmentApplicationDto$MapStructImpl implements GetRecruitmentApplicationDto.MapStruct {

    @Override
    public GetRecruitmentApplicationDto entityToDto(Application application) {
        if ( application == null ) {
            return null;
        }

        Long applicationId = null;
        String intraId = null;
        ApplicationStatus status = null;

        applicationId = application.getId();
        intraId = applicationUserIntraId( application );
        status = application.getStatus();

        GetRecruitmentApplicationDto getRecruitmentApplicationDto = new GetRecruitmentApplicationDto( applicationId, intraId, status );

        fillForms( application, getRecruitmentApplicationDto );

        return getRecruitmentApplicationDto;
    }

    private String applicationUserIntraId(Application application) {
        if ( application == null ) {
            return null;
        }
        User user = application.getUser();
        if ( user == null ) {
            return null;
        }
        String intraId = user.getIntraId();
        if ( intraId == null ) {
            return null;
        }
        return intraId;
    }
}
