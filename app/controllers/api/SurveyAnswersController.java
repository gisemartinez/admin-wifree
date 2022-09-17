package controllers.api;

import com.google.common.collect.ImmutableListMultimap;
import controllers.WiFreeController;
import controllers.api.dto.SurveyAnswersDTO;
import daos.FieldAnswerDAO;
import daos.SurveyDAO;
import models.Field;
import models.FieldAnswer;
import models.NetworkUser;
import models.Survey;
import operations.requests.SaveSurveyAnswersRequest;
import operations.responses.SaveSurveyAnswersResponse;
import play.mvc.Result;
import services.SurveysService;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Streams.forEachPair;
import static java.util.stream.Collectors.toList;

public class SurveyAnswersController extends WiFreeController {

    @Inject
    SurveysService surveysService;

    @Inject
    FieldAnswerDAO fieldAnswerDAO; // TODO quitar

    @Inject
    SurveyDAO surveyDAO; // TODO quitar

    public Result saveAnswers() throws IOException {
        logRequest();

        String requestJsonString = getRequestJsonString();
        SurveyAnswersDTO surveyAnswers = objectMapper.readValue(requestJsonString, SurveyAnswersDTO.class);
        SaveSurveyAnswersRequest request = new SaveSurveyAnswersRequest(surveyAnswers.answers, surveyAnswers.user);
        SaveSurveyAnswersResponse response = surveysService.saveSurveyAnswers(request);
        return ok("Survey answers succeeded: " + response.success() + ", survey: " + response.surveyId() + ", user: " + response.userId());
    }

    public Result answersCSV(Long surveyId) throws FileNotFoundException {
        logRequest();

        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId);
        Survey survey = surveyDAO.get(surveyId);

        List<Survey> answeredSurveysPerUser = answersForSurvey.asMap().values().stream()
                .map(userAnswers -> fillSurveyFieldsWithAnswers(survey, userAnswers))
                .collect(toList());

        String[] titles = {"id", "title", "question_number", "question", "answer"};
        List<String[]> answers = answeredSurveysPerUser.stream().flatMap(s ->
                s.getFields().stream().flatMap(f -> {
                    if ("checkbox".equals(f.getType())) {
                        return Arrays.stream(f.getConfig().getValue().split(",")).map(a -> {
                            Field copy = f.copy();
                            copy.getConfig().setValue(a.trim());
                            return copy;
                        });
                    } else {
                        return Stream.of(f);
                    }
                }).map(f -> new String[]{
                        s.getId().toString(),
                        s.getTitle(),
                        f.getConfig().getOrder().toString(),
                        f.getConfig().getLabel(),
                        f.getConfig().getValue()
                })
        ).collect(toList());
        ArrayList<String[]> strings = new ArrayList<>();
        strings.add(titles);
        strings.addAll(answers);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String date = simpleDateFormat.format(new Date());
        File csvOutputFile = new File("/tmp/survey_" + surveyId + "_answers_" + date + ".csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            strings.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }

        return ok(csvOutputFile);
    }

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    private Survey fillSurveyFieldsWithAnswers(Survey survey, java.util.Collection<FieldAnswer> userAnswers) {
        Survey answeredSurvey = survey.copy();
        Stream<Field> fields = answeredSurvey.getFields().stream().sorted(Comparator.comparing(field -> field.getConfig().getOrder()));
        Stream<FieldAnswer> answers = userAnswers.stream().sorted(Comparator.comparing(answer -> answer.getField().getConfig().getOrder()));
        forEachPair(fields, answers, (field, answer) -> answerField(answeredSurvey, field, answer));
        return answeredSurvey;
    }

    private void answerField(Survey answeredSurvey, models.Field field, FieldAnswer answer) {
        field.getConfig().setValue(answer.getAnswer());
    }

}
