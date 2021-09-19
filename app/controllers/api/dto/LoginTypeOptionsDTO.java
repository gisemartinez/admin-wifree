package controllers.api.dto;

public class LoginTypeOptionsDTO extends WiFreeDTO {

    public SurveyFormV2DTO surveyForm;

    public LoginTypeOptionsDTO() {}

    public LoginTypeOptionsDTO(SurveyFormV2DTO surveyForm) {
        this.surveyForm = surveyForm;
    }
}
