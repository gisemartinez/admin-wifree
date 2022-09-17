package controllers.api.dto;

import java.util.List;

public class SurveyAnswersDTO extends WiFreeDTO {

    public Long user;
    public List<FieldAnswerDTO> answers;

    public SurveyAnswersDTO() {
    }

    public SurveyAnswersDTO(List<FieldAnswerDTO> answers, Long user) {
        this.answers = answers;
        this.user = user;
    }
}
