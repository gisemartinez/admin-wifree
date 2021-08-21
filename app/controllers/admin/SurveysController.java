package controllers.admin;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.google.common.collect.ImmutableListMultimap;
import controllers.WiFreeController;
import daos.FieldAnswerDAO;
import models.FieldAnswer;
import models.NetworkUser;
import models.Survey;
import operations.requests.CreateSurveyRequest;
import operations.responses.CreateSurveyResponse;
import play.mvc.Result;
import services.SurveysService;

import javax.inject.Inject;

public class SurveysController extends WiFreeController {

    @Inject
    SurveysService surveysService;

    @Inject
    FieldAnswerDAO fieldAnswerDAO;

    public Result createSurvey() {
        Survey survey = formFactory.form(Survey.class).bindFromRequest().get();
        CreateSurveyResponse createSurveyResponse = surveysService.createSurvey(new CreateSurveyRequest(survey, portalId()));

        return ok(createSurveyResponse.createdSurvey().toLogString());
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result getSurveyAnswers(Long surveyId) {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId); // TODO quitar a una Function

        String rr = mapToString(answersForSurvey);

        return ok(rr); // TODO: devolver lo que realmente necesite la vista
    }

    // TODO quitar, es de prueba
    private String mapToString(ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey) {
        StringBuilder rr = new StringBuilder();
        for (NetworkUser networkUser : answersForSurvey.keySet()) {
            StringBuilder r = new StringBuilder("user: " + networkUser.getId() + "\n");
            for (FieldAnswer fieldAnswer : answersForSurvey.get(networkUser)) {
                r.append("  ");
                r.append(fieldAnswer.toLogString());
                r.append("\n");
            }
            rr.append(r.toString());
        }
        return rr.toString();
    }

}
