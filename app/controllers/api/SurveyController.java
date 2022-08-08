package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.WiFreeController;
import daos.PortalDAO;
import models.*;
import operations.requests.CreateSurveyRequest;
import operations.responses.CreateSurveyResponse;
import play.mvc.Result;
import services.SurveysService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SurveyController extends WiFreeController {

    @Inject
    private SurveysService surveysService;

    @Inject
    private PortalDAO portalDAO;

    public Result saveSurvey() {
        JsonNode bodyJson = request().body().asJson();

        final long id = bodyJson.findValue("id").asLong();
        final long portalId = bodyJson.findValue("portalId").asLong();
        final Portal portal = portalDAO.get(portalId);
        final String title = bodyJson.findValue("title").asText();
        List<Field> fields = new ArrayList<>();

        bodyJson.withArray("fields")
                .elements()
                .forEachRemaining(fieldNode -> createField(fields, fieldNode));

        final Survey survey = new Survey(id, portal, title, fields, true);
        survey.getFields().forEach(field -> field.setSurvey(survey));

        // TODO guardar survey, crear dao
        CreateSurveyResponse createSurveyResponse = surveysService.createSurvey(new CreateSurveyRequest(survey,
                                                                                                        portalId));

        return ok(createSurveyResponse.isOk() + survey.getTitle());
    }

    private void createField(List<Field> fields, JsonNode field) {
        final long fieldId = field.findValue("id").asLong();
        final String fieldType = field.findValue("type").asText();
        final JsonNode fieldConfigValue = field.findValue("config");

        final String key = fieldConfigValue.findValue("key").asText();
        final String label = fieldConfigValue.findValue("label").asText();
        final int order = fieldConfigValue.findValue("order").asInt();
        FieldConfig fieldConfig;
        
        switch (fieldType) {
            case FieldConfig.FieldConfigTypes.Textbox:
                fieldConfig = createTextboxConfig(fieldConfigValue, key, label, order);
                break;
            case FieldConfig.FieldConfigTypes.Rating:
                fieldConfig = createRatingFieldConfig(fieldConfigValue, key, label, order);
                break;
            case FieldConfig.FieldConfigTypes.Radio:
            case FieldConfig.FieldConfigTypes.Checkbox:
                fieldConfig = createRadioFieldConfig(fieldConfigValue, key, label, order);
                break;
            default:
                throw new RuntimeException("Error parsing received survey, unknown fieldType: " + fieldType);
        }

        fields.add(new Field(fieldId, fieldType, fieldConfig));
    }

    private FieldConfig createTextboxConfig(JsonNode fieldConfigValue, String key, String label, int order) {
        FieldConfig fieldConfig;
        boolean required = Optional.ofNullable(fieldConfigValue.findValue("required")).map(JsonNode::asBoolean).orElse(
                false);
        String value = Optional.ofNullable(fieldConfigValue.findValue("value")).map(JsonNode::asText).orElse(null);
        fieldConfig = new TextboxFieldConfig(key, label, required, order, value);
        return fieldConfig;
    }

    private FieldConfig createRatingFieldConfig(JsonNode fieldConfigValue, String key, String label, int order) {
        FieldConfig fieldConfig;
        int maximum = fieldConfigValue.findValue("maximum").asInt();
        fieldConfig = new RatingFieldConfig(key, label, order, maximum);
        return fieldConfig;
    }

    private FieldConfig createRadioFieldConfig(JsonNode fieldConfigValue, String key, String label, int order) {
        FieldConfig fieldConfig;
        final List<Option> options = new ArrayList<>();
        fieldConfigValue.withArray("options")
                        .elements()
                        .forEachRemaining(optionsNode -> createOption(options, optionsNode));
        fieldConfig = new RadioFieldConfig(key, label, order, options);
        return fieldConfig;
    }

    private void createOption(List<Option> options, JsonNode optionsNode) {
        Integer optionIndex = optionsNode.findValue("index").asInt();
        String optionKey = optionsNode.findValue("key").asText();
        options.add(new Option(optionIndex, optionKey));
    }
}
