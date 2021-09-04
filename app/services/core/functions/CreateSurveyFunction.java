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

import java.util.function.Function;

@SuppressWarnings("unused")
public class CreateSurveyFunction extends WiFreeFunction<CreateSurveyRequest, CreateSurveyResponse> {

    @Override
    public Function<CreateSurveyRequest, CreateSurveyResponse> function() {
        function = request -> {
            SurveyDAO surveyDAO = new SurveyDAO();
            Survey survey = request.survey();
            removeFirstEmptyField(survey);
            fixFields(survey);
            setPortal(survey, request.portalId());
            surveyDAO.saveOrUpdate(survey);
            return new CreateSurveyResponse(survey, true, null);
        };
        return function;
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
        Portal portal = new PortalDAO().get(portalId);
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
