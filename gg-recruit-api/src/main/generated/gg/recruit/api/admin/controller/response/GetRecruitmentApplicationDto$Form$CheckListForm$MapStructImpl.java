package gg.recruit.api.admin.controller.response;

import gg.data.recruit.recruitment.CheckList;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T03:22:03+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.16.1 (Oracle Corporation)"
)
public class GetRecruitmentApplicationDto$Form$CheckListForm$MapStructImpl implements GetRecruitmentApplicationDto.Form.CheckListForm.MapStruct {

    @Override
    public GetRecruitmentApplicationDto.Form.CheckListForm entityToDto(CheckList checkList) {
        if ( checkList == null ) {
            return null;
        }

        Long checkId = null;
        String content = null;

        checkId = checkList.getId();
        content = checkList.getContent();

        GetRecruitmentApplicationDto.Form.CheckListForm checkListForm = new GetRecruitmentApplicationDto.Form.CheckListForm( checkId, content );

        return checkListForm;
    }
}
