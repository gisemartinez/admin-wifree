package services.core.functions;

import daos.FieldAnswerDAO;
import daos.FieldDAO;
import daos.NetworkUserDAO;
import models.Field;
import models.FieldAnswer;
import models.NetworkUser;
import operations.requests.SaveSurveyAnswersRequest;
import operations.responses.SaveSurveyAnswersResponse;
import play.Logger;
import services.core.ServiceType;
import services.core.WiFreeFunction;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveSurveyAnswersFunction extends WiFreeFunction<SaveSurveyAnswersRequest, SaveSurveyAnswersResponse> {

    protected final Logger.ALogger logger = Logger.of(this.getClass());

    @Override
    public Function<SaveSurveyAnswersRequest, SaveSurveyAnswersResponse> function() {
        function = request -> {
            try {
                NetworkUserDAO networkUserDAO = new NetworkUserDAO();
                FieldDAO fieldDAO = new FieldDAO();
                long userId = request.userId();

                NetworkUser networkUser = networkUserDAO.get(userId);
                List<FieldAnswer> surveyAnswers = request.fieldsAnswers().stream()
                        .map(fieldAnswer -> toModel(networkUser, fieldDAO, fieldAnswer))
                        .collect(Collectors.toList());

                FieldAnswerDAO fieldAnswerDAO = new FieldAnswerDAO();
                fieldAnswerDAO.saveAll(surveyAnswers);

                Long surveyId = surveyAnswers.stream().map(x -> x.getField().getSurvey().getId()).distinct().findAny().orElse(0L);

                return new SaveSurveyAnswersResponse(true, surveyId, userId);
            } catch (Exception e) {
                logger.error("Error saving survey answers. Request: " + request.toString(), e);
                return new SaveSurveyAnswersResponse(false, 0, 0);
            }
        };
        return function;
    }

    private FieldAnswer toModel(NetworkUser networkUser, FieldDAO fieldDAO, controllers.api.dto.FieldAnswerDTO fieldAnswer) {
        Field field = fieldDAO.get(fieldAnswer.field);
        return new FieldAnswer(field, networkUser, fieldAnswer.answer);
    }

    @Override
    public Class<SaveSurveyAnswersRequest> rqClass() {
        return SaveSurveyAnswersRequest.class;
    }

    @Override
    public Class<SaveSurveyAnswersResponse> rsClass() {
        return SaveSurveyAnswersResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }
}
