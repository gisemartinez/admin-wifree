package controllers.admin;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableListMultimap;
import controllers.WiFreeController;
import controllers.routes;
import daos.FieldAnswerDAO;
import daos.SurveyDAO;
import models.*;
import operations.requests.CreateSurveyRequest;
import operations.responses.CreateSurveyResponse;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import services.SurveysService;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
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

        return redirect(routes.AdminAppController.allSurveys());
    }

    public Result getSurveyAnswers(Long surveyId) {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId); // TODO quitar a una Function

        String rr = mapToString(answersForSurvey);

        return ok(rr); // TODO: devolver lo que realmente necesite la vista
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result answeredSurvey(Long surveyId, Integer offset) throws JsonProcessingException {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId);

        if (answersForSurvey.isEmpty()) {
            return ok(views.html.admin.survey_no_answers.render(getCurrentProfile()));
        } else {
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
        }
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result getSurveyResults(Long surveyId) {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId);
        Survey survey = surveyDAO.get(surveyId);
        List<Survey> surveys = answersForSurvey.asMap().values().stream()
                .map(userAnswers -> fillSurveyFieldsWithAnswers(survey, userAnswers))
                .collect(toList());

        List<SummarizedAnswers> summarizedAnswers = surveys.stream()
                .flatMap(s -> s.getFields().stream().map(field -> {
                    FieldConfig config = field.getConfig();
                    String question = config.getLabel();
                    String type = field.getType();
                    String answer = config.getValue();
                    Integer order = config.getOrder();
                    Integer maximum = config.getMaximum();
                    List<String> possibleAnswers = config.hasOptions()
                            ? config.getOtherOptions().stream().map(Option::getKey).collect(toList())
                            : new ArrayList<>();
                    return new QuestionAnswer(question, type, answer, order, possibleAnswers, maximum);
                }))
                .collect(toImmutableListMultimap(x -> x.question, Function.identity()))
                .asMap().entrySet().stream()
                .map(entry -> {
                    String question = entry.getKey();
                    Collection<QuestionAnswer> answers = entry.getValue();
                    String type = answers.stream().findAny().map(x -> x.type).orElse("");
                    Integer order = answers.stream().findAny().map(x -> x.order).orElse(-1);
                    Integer maximum = answers.stream().findAny().map(x -> x.maximum).orElse(0);
                    Map<String, Long> answersOccurrences = answers.stream().collect(Collectors.groupingBy(x -> x.answer, Collectors.counting()));
                    if ("rating".equals(type)) {
                        IntStream.range(1, maximum+1)
                                .forEachOrdered(i -> answersOccurrences.putIfAbsent(String.valueOf(i), 0L));
                    } else {
                        answers.stream()
                                .flatMap(qa -> qa.possibleAnswers.stream())
                                .forEachOrdered(a -> answersOccurrences.putIfAbsent(a, 0L));
                    }
                    return new SummarizedAnswers(question, type, answersOccurrences, order);
                })
                .collect(toList());

        List<AnswersJson> answers = summarizedAnswers.stream()
                .map(x -> {
                    List<String> labels = new ArrayList<>();
                    List<Long> values = new ArrayList<>();
                    x.answers.forEach((key, value) -> {
                        labels.add(key);
                        values.add(value);
                    });
                    return new AnswersJson(x.question, x.id, x.type, x.order, labels, values);
                })
                .collect(toList());

        DataJson dataJson = new DataJson(answers);
        return ok(views.html.admin.surveys_results.render(getCurrentProfile(), dataJson));
    }

    public static class DataJson {
        public List<AnswersJson> data;

        public DataJson() {}

        public String toJsonString() {
            return Json.toJson(this).toString();
        }

        public DataJson(List<AnswersJson> data) {
            this.data = data;
        }

        public List<AnswersJson> getData() {
            return data;
        }
    }

    public static class AnswersJson {
        public final String question;
        public final String id;
        public final String type;
        public final Integer order;
        public final List<String> labels;
        public final List<Long> values;

        public AnswersJson(String question, String id, String type, Integer order, List<String> labels, List<Long> values) {
            this.question = question;
            this.id = id;
            this.type = type;
            this.order = order;
            this.labels = labels;
            this.values = values;
        }

        public String average() {
            double average = values.stream().filter(n -> n > 0L).mapToInt(Long::intValue).average().orElse(0d);
            return String.format("%,.2f", average);
        }

        public String getQuestion() {
            return question;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public Integer getOrder() {
            return order;
        }

        public List<String> getLabels() {
            return labels;
        }

        public List<Long> getValues() {
            return values;
        }
    }

    public static class SummarizedAnswers {
        public final String question;
        public final String type;
        public final Map<String, Long> answers;
        public final Integer order;
        public final String id;

        public SummarizedAnswers(String question, String type, Map<String, Long> answers, Integer order) {
            this.question = question;
            this.type = type;
            this.answers = answers;
            this.order = order;
            this.id = "chart_" + order;
        }
    }

    public static class QuestionAndType {
        public final String question;
        public final String type;

        public QuestionAndType(String question, String type) {
            this.question = question;
            this.type = type;
        }
    }

    public static class GroupedAnswers {
        public final Map<String, String> answers;
        public final String type;

        public GroupedAnswers(Map<String, String> answers, String type) {
            this.answers = answers;
            this.type = type;
        }
    }

    public static class QuestionAnswer {
        public final String question;
        public final String type;
        public final String answer;
        public final int order;
        public final List<String> possibleAnswers;
        public final Integer maximum;

        public QuestionAnswer(String question, String type, String answer, int order, List<String> possibleAnswers, Integer maximum) {
            this.question = question;
            this.type = type;
            this.answer = answer;
            this.order = order;
            this.possibleAnswers = possibleAnswers;
            this.maximum = maximum;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
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
