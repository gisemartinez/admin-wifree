package services.core.functions;

import daos.SurveyDAO;
import models.Survey;
import operations.requests.GetAllSurveysRequest;
import operations.responses.GetAllSurveysResponse;
import services.core.ServiceType;
import services.core.WiFreeFunction;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;

public class GetAllSurveysFunction extends WiFreeFunction<GetAllSurveysRequest, GetAllSurveysResponse> {

    @Override
    public Function<GetAllSurveysRequest, GetAllSurveysResponse> function() {
        function = request -> {
            try {
                SurveyDAO surveyDAO = new SurveyDAO();
                List<Survey> allSurveys = surveyDAO.findByPortal(request.portalId());
                return new GetAllSurveysResponse(allSurveys, true, null);
            } catch (Exception ex) {
                return new GetAllSurveysResponse(emptyList(), false, null);
            }

        };
        return function;
    }

    @Override
    public Class<GetAllSurveysRequest> rqClass() {
        return GetAllSurveysRequest.class;
    }

    @Override
    public Class<GetAllSurveysResponse> rsClass() {
        return GetAllSurveysResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }

}
