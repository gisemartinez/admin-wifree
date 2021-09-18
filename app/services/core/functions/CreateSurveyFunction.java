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
import java.util.function.Function;

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
            long portalId = request.portalId();
            removeFirstEmptyField(survey);
            fixFields(survey);
            setPortal(survey, portalId);
            toggleEnabledSurveys(portalId, survey);
            surveyDAO.saveOrUpdate(survey);
            return new CreateSurveyResponse(survey, true, null);
        };
        return function;
    }

    private void toggleEnabledSurveys(long portalId, Survey survey) {
        if (survey.isEnabled())
            surveyDAO.disableAll(portalId);
    }

    private void fixFields(Survey survey) {
        survey.getFields().forEach(field -> {
            switch (field.getType()) {
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

    private void removeFirstEmptyField(Survey survey) {
        Field firstField = survey.getFields().get(0);
        if (StringHelper.isEmpty(firstField.getType())) survey.getFields().remove(firstField);
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
