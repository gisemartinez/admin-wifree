package services.core.functions;

import daos.FieldAnswerDAO;
import daos.FieldDAO;
import daos.NetworkUserDAO;
import models.Field;
import models.FieldAnswer;
import models.NetworkUser;
import models.Portal;
import models.types.Gender;
import operations.requests.SaveSurveyAnswersRequest;
import operations.responses.SaveSurveyAnswersResponse;
import play.Logger;
import services.core.ServiceType;
import services.core.WiFreeFunction;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveSurveyAnswersFunction extends WiFreeFunction<SaveSurveyAnswersRequest, SaveSurveyAnswersResponse> {

    protected final Logger.ALogger logger = Logger.of(this.getClass());

    @Inject
    private NetworkUserDAO networkUserDAO;

    @Inject
    private FieldDAO fieldDAO;

    @Inject
    private FieldAnswerDAO fieldAnswerDAO;

    @Override
    public Function<SaveSurveyAnswersRequest, SaveSurveyAnswersResponse> function() {
        function = request -> {
            try {
                long userId = request.userId();

                NetworkUser networkUser = networkUserDAO.get(userId);

                if (networkUser == null) {
                    Portal portal = fieldDAO.findPortal(request.fieldsAnswers().get(0).field);
                    networkUser = new NetworkUser(portal, "Usuario " + userId, "usuario_" + userId + "@mail.com", "", Instant.now(),
                            true, "", Gender.Undefined, null, null, null, 0);
                    networkUser.setId(userId);
                    networkUserDAO.save(networkUser);
                }

                NetworkUser finalNetworkUser = networkUser;
                List<FieldAnswer> surveyAnswers = request.fieldsAnswers().stream()
                        .map(fieldAnswer -> toModel(finalNetworkUser, fieldAnswer))
                        .collect(Collectors.toList());

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

    private FieldAnswer toModel(NetworkUser networkUser, controllers.api.dto.FieldAnswerDTO fieldAnswer) {
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
