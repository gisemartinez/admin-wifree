package controllers.api.dto;


import models.Survey;

import java.util.List;
import java.util.stream.Collectors;

public class SurveyFormV2DTO extends WiFreeDTO {

    public String id;
    public String title;
    public List<FieldDTO> fields;

    public SurveyFormV2DTO() {}

    public SurveyFormV2DTO(String id, String title, List<FieldDTO> fields) {
        this.id = id;
        this.title = title;
        this.fields = fields;
    }

    public static SurveyFormV2DTO fromDomain(Survey survey) {
        List<FieldDTO> fields = survey.getFields().stream().map(FieldDTO::fromDomain).collect(Collectors.toList());
        return new SurveyFormV2DTO(survey.getId().toString(), survey.getTitle(), fields);
    }
}
