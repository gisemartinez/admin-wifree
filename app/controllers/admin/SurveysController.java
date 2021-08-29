package controllers.admin;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableListMultimap;
import controllers.WiFreeController;
import daos.FieldAnswerDAO;
import daos.SurveyDAO;
import models.Field;
import models.FieldAnswer;
import models.NetworkUser;
import models.Survey;
import operations.requests.CreateSurveyRequest;
import operations.responses.CreateSurveyResponse;
import play.data.Form;
import play.mvc.Result;
import services.SurveysService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Streams.*;
import static java.util.stream.Collectors.toList;

public class SurveysController extends WiFreeController {

    @Inject
    SurveysService surveysService;

    @Inject
    FieldAnswerDAO fieldAnswerDAO; // TODO quitar

    @Inject
    SurveyDAO surveyDAO; // TODO quitar

    public Result createSurvey() {
        Survey survey = formFactory.form(Survey.class).bindFromRequest().get();
        CreateSurveyResponse createSurveyResponse = surveysService.createSurvey(new CreateSurveyRequest(survey, portalId()));

        return ok(createSurveyResponse.createdSurvey().toLogString());
    }

    public Result getSurveyAnswers(Long surveyId) {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId); // TODO quitar a una Function

        String rr = mapToString(answersForSurvey);

        return ok(rr); // TODO: devolver lo que realmente necesite la vista
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result answeredSurvey(Long surveyId, Integer offset) throws JsonProcessingException {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId);
        Survey survey = surveyDAO.get(surveyId);

        List<Survey> answeredSurveysPerUser = answersForSurvey.asMap().values().stream()
                .map(userAnswers -> fillSurveyFieldsWithAnswers(survey, userAnswers))
                .collect(toList());

        int totalAnswers = answeredSurveysPerUser.size();
        int sanitizedOffset = totalAnswers > 0 ? Math.min(Math.max(offset, 0), totalAnswers - 1) : 0;

        Form<Survey> form = formFactory.form(Survey.class);
        if (totalAnswers > 0) form = form.fill(answeredSurveysPerUser.get(sanitizedOffset));

        return ok(
                views.html.admin.surveys.render(
                        getCurrentProfile(),
                        form,
                        false,
                        false,
                        sanitizedOffset + 1,
                        totalAnswers)
        );
//        return ok(answeredSurveysPerUser.stream().map(Survey::toLogString).collect(Collectors.joining("\n\n"))); // TODO: devolver algo para la vista
    }

    @SuppressWarnings("UnstableApiUsage")
    private Survey fillSurveyFieldsWithAnswers(Survey survey, java.util.Collection<FieldAnswer> userAnswers) {
        Survey answeredSurvey = survey.copy();
        Stream<Field> fields = answeredSurvey.getFields().stream();
        Stream<FieldAnswer> answers = userAnswers.stream();
        forEachPair(fields, answers, (field, answer) -> answerField(answeredSurvey, field, answer));
        return answeredSurvey;
    }

    private void answerField(Survey answeredSurvey, models.Field field, FieldAnswer answer) {
        field.getConfig().setValue(answer.getAnswer());
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
