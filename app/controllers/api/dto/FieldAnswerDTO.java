package controllers.api.dto;


public class FieldAnswerDTO extends WiFreeDTO {

    public Long field;
    public String answer;

    public FieldAnswerDTO() {
    }

    public FieldAnswerDTO(Long field, String answer) {
        this.field = field;
        this.answer = answer;
    }
}
