package services.core.functions;

import daos.PortalDAO;
import daos.SurveyDAO;
import models.Field;
import models.Portal;
import models.Survey;
import operations.requests.CreateSurveyRequest;
import operations.responses.CreateSurveyResponse;
import services.core.ServiceType;
import services.core.WiFreeFunction;
import utils.StringHelper;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static scala.collection.JavaConverters.asScalaBuffer;

@SuppressWarnings("unused")
public class CreateSurveyFunction extends WiFreeFunction<CreateSurveyRequest, CreateSurveyResponse> {

    @Inject
    SurveyDAO surveyDAO;

    @Inject
    PortalDAO portalDAO;

    @Override
    public Function<CreateSurveyRequest, CreateSurveyResponse> function() {
        function = request -> {
            Survey survey = request.survey();
            boolean surveyExists = surveyDAO.findById(survey.getId()).isPresent();
            long portalId = request.portalId();
            removeEmptyFields(survey);
            fixFields(survey);
            setPortal(survey, portalId);
            toggleEnabledSurveys(portalId, survey);
            if (survey.getFields().isEmpty()) {
                return new CreateSurveyResponse(survey, false, asScalaBuffer(Collections.singletonList("Por favor, ingrese opciones en la encuesta")).toList());
            }
            if (surveyExists) {
                surveyDAO.saveOrUpdate(survey);
            }
            else {
                surveyDAO.save(survey);
            }
            return new CreateSurveyResponse(survey, true, null);
        };
        return function;
    }

    private void toggleEnabledSurveys(long portalId, Survey survey) {
        if (survey.isEnabled()) {
            surveyDAO.disableAll(portalId);
        }
    }

    private void fixFields(Survey survey) {
        survey.getFields().forEach(field -> {
            field.setSurvey(survey);
            switch (field.getType()) {
                case "rating":
                case "textbox":
                    field.getConfig().setOtherOptions(null);
                    break;
            }
        });
    }

    private void setPortal(Survey survey, long portalId) {
        Portal portal = portalDAO.get(portalId);
        survey.setPortal(portal);
    }

    private void removeEmptyFields(Survey survey) {
        List<Field> fields = survey.getFields().stream().filter(f -> StringHelper.isNotEmpty(f.getType())).collect(Collectors.toList());
        survey.setFields(fields);
    }

    @Override
    public Class<CreateSurveyRequest> rqClass() {
        return CreateSurveyRequest.class;
    }

    @Override
    public Class<CreateSurveyResponse> rsClass() {
        return CreateSurveyResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }

}
