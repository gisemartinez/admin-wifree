package controllers.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import models.Portal;
import models.Survey;
import play.libs.Json;

import java.util.List;
import java.util.stream.Collectors;

public class SurveyLoginPortalConfigDTO extends WiFreeDTO {

    @JsonProperty("login-type")
    public final String loginType;
    @JsonProperty("survey-form")
    public final SurveyFormDTO surveyForm;

    public static JsonNode json(Portal portal, Survey survey) {
        return new SurveyLoginPortalConfigDTO(portal, survey).toJson();
    }

    private SurveyLoginPortalConfigDTO(Portal portal, Survey survey) {
        List<JsonNode> fields = survey.getFields().stream().map(Json::toJson).collect(Collectors.toList());
        SurveyFormDTO surveyForm = new SurveyFormDTO(survey.getId().toString(), survey.getTitle(), fields);
        this.loginType = "survey";
        this.surveyForm = surveyForm;
    }

    public static class SurveyFormDTO {
        public final String id;
        public final String title;
        public final List<JsonNode> fields;

        SurveyFormDTO(String id, String title, List<JsonNode> fields) {
            this.id = id;
            this.title = title;
            this.fields = fields;
        }
    }
}
