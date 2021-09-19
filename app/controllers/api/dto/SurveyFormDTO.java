package controllers.api.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class SurveyFormDTO extends WiFreeDTO {

    public String id;
    public String title;
    public List<JsonNode> fields;

    public SurveyFormDTO() {}

    public SurveyFormDTO(String id, String title, List<JsonNode> fields) {
        this.id = id;
        this.title = title;
        this.fields = fields;
    }
}
