package gg.recruit.api.admin.controller.response;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.enums.InputType;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T03:22:03+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 11.0.16.1 (Oracle Corporation)"
)
public class GetRecruitmentApplicationDto$Form$MapStructImpl implements GetRecruitmentApplicationDto.Form.MapStruct {

    @Override
    public GetRecruitmentApplicationDto.Form entityToDto(Question question) {
        if ( question == null ) {
            return null;
        }

        Long questionId = null;
        String question1 = null;
        InputType inputType = null;

        questionId = question.getId();
        question1 = question.getQuestion();
        inputType = question.getInputType();

        GetRecruitmentApplicationDto.Form form = new GetRecruitmentApplicationDto.Form( questionId, question1, inputType );

        return form;
    }
}
