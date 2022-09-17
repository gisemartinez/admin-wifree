package controllers.api.dto;


import models.Survey;

import java.util.List;
import java.util.stream.Collectors;

public class SurveyFormDTO extends WiFreeDTO {

    public String id;
    public String title;
    public List<FieldDTO> fields;

    public SurveyFormDTO() {
    }

    public SurveyFormDTO(String id, String title, List<FieldDTO> fields) {
        this.id = id;
        this.title = title;
        this.fields = fields;
    }

    public static SurveyFormDTO fromDomain(Survey survey) {
        List<FieldDTO> fields = survey.getFields().stream().map(FieldDTO::fromDomain).collect(Collectors.toList());
        return new SurveyFormDTO(survey.getId().toString(), survey.getTitle(), fields);
    }
}
