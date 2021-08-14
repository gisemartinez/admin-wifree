package controllers.api;

import controllers.WiFreeController;
import controllers.api.dto.SurveyAnswersDTO;
import operations.requests.SaveSurveyAnswersRequest;
import operations.responses.SaveSurveyAnswersResponse;
import play.mvc.Result;
import services.SurveysService;

import javax.inject.Inject;
import java.io.IOException;

public class SurveyAnswersController extends WiFreeController {

    @Inject
    SurveysService surveysService;

    public Result saveAnswers() throws IOException {
        SurveyAnswersDTO surveyAnswers = objectMapper.readValue(getRequestJsonString(), SurveyAnswersDTO.class);
        SaveSurveyAnswersRequest request = new SaveSurveyAnswersRequest(surveyAnswers.answers, surveyAnswers.user);
        SaveSurveyAnswersResponse response = surveysService.saveSurveyAnswers(request);
        return ok("Survey answers succeeded: " + response.success() + ", survey: " + response.surveyId() + ", user: " + response.userId());
    }

}
