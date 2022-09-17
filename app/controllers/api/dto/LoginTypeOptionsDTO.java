package controllers.api.dto;

import java.util.List;

public class LoginTypeOptionsDTO extends WiFreeDTO {

    public SurveyFormDTO surveyForm;
    public List<SocialMediaKeysDTO> socialMediaKeys;

    public LoginTypeOptionsDTO() {
    }

    public LoginTypeOptionsDTO(SurveyFormDTO surveyForm) {
        this.surveyForm = surveyForm;
    }

    public LoginTypeOptionsDTO(List<SocialMediaKeysDTO> socialMediaKeys) {
        this.socialMediaKeys = socialMediaKeys;
    }
}
