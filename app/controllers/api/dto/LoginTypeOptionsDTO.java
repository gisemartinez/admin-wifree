package controllers.api.dto;

import java.util.List;

public class LoginTypeOptionsDTO extends WiFreeDTO {

    public SurveyFormDTO surveyForm;
    public List<SocialMediaKeysDTO> socialMediaKeys;
    public List<String> images;

    public LoginTypeOptionsDTO() {}

    public LoginTypeOptionsDTO(SurveyFormDTO surveyForm, List<String> images) {
        this.surveyForm = surveyForm;
        this.images = images;
    }

    public LoginTypeOptionsDTO(List<SocialMediaKeysDTO> socialMediaKeys, List<String> images) {
        this.socialMediaKeys = socialMediaKeys;
        this.images = images;
    }
}
