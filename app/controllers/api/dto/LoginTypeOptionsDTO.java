package controllers.api.dto;

public class LoginTypeOptionsDTO extends WiFreeDTO {

    public SurveyFormDTO surveyForm;
    public SocialMediaKeysDTO socialMediaKeys;

    public LoginTypeOptionsDTO() {}

    public LoginTypeOptionsDTO(SurveyFormDTO surveyForm) {
        this.surveyForm = surveyForm;
    }

    public LoginTypeOptionsDTO(SocialMediaKeysDTO socialMediaKeys) {
        this.socialMediaKeys = socialMediaKeys;
    }
}
