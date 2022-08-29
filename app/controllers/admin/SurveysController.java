package controllers.admin;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableListMultimap;
import controllers.WiFreeController;
import daos.FieldAnswerDAO;
import daos.PortalDAO;
import daos.SurveyDAO;
import models.*;
import operations.requests.CreateSurveyRequest;
import operations.requests.GetAllSurveysRequest;
import operations.responses.CreateSurveyResponse;
import operations.responses.GetAllSurveysResponse;
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
import static com.google.common.collect.Streams.forEachPair;
import static java.util.stream.Collectors.toList;

public class SurveysController extends WiFreeController {

    @Inject
    SurveysService surveysService;

    @Inject
    FieldAnswerDAO fieldAnswerDAO;

    @Inject
    SurveyDAO surveyDAO;

    @Inject
    private PortalDAO portalDAO;

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result allSurveys() throws NoProfileFoundException {
        GetAllSurveysResponse getAllSurveysResponse = surveysService.getAllSurveys(new GetAllSurveysRequest(portalId()));
        return ok(render(views.html.admin.all_surveys.render(SurveyFormHelper.buildSummaries(getAllSurveysResponse))));
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result survey(Long surveyId) throws NoProfileFoundException {
        Survey survey = new SurveyDAO().get(surveyId);
        Form<Survey> form = formFactory.form(Survey.class).fill(survey);
        return ok(render(views.html.admin.surveys.render(form, true, false, 0, 0)));
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result createSurvey() {
        Survey survey = formFactory.form(Survey.class).bindFromRequest().get();
        CreateSurveyResponse s = surveysService.createSurvey(new CreateSurveyRequest(survey, portalId()));
        if (!s.isOk()) {
            flash("Error", s.errors().head());
            return badRequest(render(views.html.admin.surveys.render(formFactory.form(Survey.class).fill(s.createdSurvey()).withError("Error", s.errors().head()), true, false, 0, 0)));
        } else {
            flash("Success", "");
            return redirect(controllers.admin.routes.SurveysController.allSurveys());
        }
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result deleteSurvey(Long surveyId) {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId);
        if (answersForSurvey.isEmpty()) {
            Survey survey = surveyDAO.get(surveyId);
            surveysService.deleteSurvey(survey);
            return redirect(controllers.admin.routes.SurveysController.allSurveys());
        } else {
            return redirect(controllers.admin.routes.SurveysController.getSurveyAnswers(surveyId));
        }
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result getSurveyAnswers(Long surveyId) {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId); // TODO quitar a una Function

        String rr = mapToString(answersForSurvey);

        return ok(rr); // TODO: devolver lo que realmente necesite la vista
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result answeredSurvey(Long surveyId, Integer offset) throws JsonProcessingException {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId);

        if (answersForSurvey.isEmpty()) {
            return ok(render(views.html.admin.survey_no_answers.render()));
        } else {
            Survey survey = surveyDAO.get(surveyId);

            List<Survey> answeredSurveysPerUser = answersForSurvey.asMap()
                                                                  .values()
                                                                  .stream()
                                                                  .map(userAnswers -> fillSurveyFieldsWithAnswers(survey,
                                                                                                                  userAnswers))
                                                                  .collect(toList());

            int totalAnswers = answeredSurveysPerUser.size();
            int sanitizedOffset = totalAnswers > 0 ? Math.min(Math.max(offset, 0), totalAnswers - 1) : 0;

            Form<Survey> form = formFactory.form(Survey.class);
            if (totalAnswers > 0) {
                form = form.fill(answeredSurveysPerUser.get(sanitizedOffset));
            }

            return ok(render(views.html.admin.surveys.render(form, false, false, sanitizedOffset + 1, totalAnswers)));
        }
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result getSurveyResults(Long surveyId) {
        ImmutableListMultimap<NetworkUser, FieldAnswer> answersForSurvey = fieldAnswerDAO.findForSurvey(surveyId);
        Survey survey = surveyDAO.get(surveyId);
        List<Survey> surveys = answersForSurvey
                .asMap()
                .values()
                .stream()
                .map(userAnswers -> fillSurveyFieldsWithAnswers(survey, userAnswers))
                .collect(toList());

        Stream<Field> fieldStream = surveys.stream().flatMap(s -> s.getFields().stream().flatMap(f -> {
            if (FieldConfig.FieldConfigTypes.Checkbox.equals(f.getType())) {
                return Arrays.stream(f.getConfig().getValue().split(",")).map(a -> {
                    Field copy = f.copy();
                    copy.getConfig().setValue(a.trim());
                    return copy;
                });
            } else {
                return Stream.of(f);
            }
        }));

        List<SummarizedAnswers> summarizedAnswers = fieldStream
                .map(field -> {
                    FieldConfig config = field.getConfig();
                    String question = config.getLabel();
                    String type = field.getType();
                    String answer = config.getValue();
                    Integer order = config.getOrder();
                    Integer maximum = config.getMaximum();
                    List<String> possibleAnswers = config
                            .hasOptions() ? config.getOtherOptions()
                                                  .stream()
                                                  .map(Option::getKey)
                                                  .collect(
                                                          toList()) : new ArrayList<>();
                    return new QuestionAnswer(question,
                                              type,
                                              answer,
                                              order,
                                              possibleAnswers,
                                              maximum);
                })
                .collect(toImmutableListMultimap(x -> x.question,
                                                 Function.identity()))
                .asMap()
                .entrySet()
                .stream()
                .map(entry -> {
                    String question = entry.getKey();
                    Collection<QuestionAnswer> answers = entry.getValue();
                    String type = answers.stream()
                                         .findAny()
                                         .map(x -> x.type)
                                         .orElse("");
                    Integer order = answers.stream()
                                           .findAny()
                                           .map(x -> x.order)
                                           .orElse(-1);
                    Integer maximum = answers.stream()
                                             .findAny()
                                             .map(x -> x.maximum)
                                             .orElse(0);
                    Map<String, Long> answersOccurrences = answers.stream()
                                                                  .collect(
                                                                          Collectors.groupingBy(
                                                                                  x -> x.answer,
                                                                                  Collectors.counting()));
                    if (FieldConfig.FieldConfigTypes.Rating.equals(type)) {
                        IntStream.range(1, maximum + 1)
                                 .forEachOrdered(i -> answersOccurrences.putIfAbsent(
                                         String.valueOf(i),
                                         0L));
                    } else {
                        answers.stream()
                               .flatMap(qa -> qa.possibleAnswers.stream())
                               .forEachOrdered(a -> answersOccurrences.putIfAbsent(
                                       a,
                                       0L));
                    }
                    return new SummarizedAnswers(question,
                                                 type,
                                                 answersOccurrences,
                                                 order);
                })
                .collect(toList());

        List<AnswersJson> answers = buildAnswersJson(summarizedAnswers);

        DataJson dataJson = new DataJson(answers);
        return ok(render(views.html.admin.surveys_results.render(dataJson)));
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result surveys() throws NoProfileFoundException {
        ArrayList<Field> fields = new ArrayList<>();
        Field ageField = new Field(null, "textbox", new FieldConfig(null, "Edad", 1, true, null, null, null));
        Field genreField = new Field(null, "radio", new FieldConfig(null, "Género", 2, true, null, null, Arrays.asList(new Option(1, "Femenino"), new Option(2, "Masculino"), new Option(3, "Otro"))));
        fields.add(ageField);
        fields.add(genreField);
        Survey survey = new Survey((new Random()).nextLong(), null, null, fields, false);
        ageField.setSurvey(survey);
        genreField.setSurvey(survey);

        Form<Survey> form = formFactory.form(Survey.class).fill(survey);

        return ok(render(views.html.admin.surveys.render(form, true, false, 0, 0)));
    }

    @SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
    public Result saveSurvey() {
        JsonNode bodyJson = request().body().asJson();

        final long id = bodyJson.findValue("id").asLong();
        final long portalId = bodyJson.findValue("portalId").asLong();
        final Portal portal = portalDAO.get(portalId);
        final String title = bodyJson.findValue("title").asText();
        List<Field> fields = new ArrayList<>();

        bodyJson.withArray("fields")
                .elements()
                .forEachRemaining(fieldNode -> createField(fields, fieldNode));

        final Survey survey = new Survey(id, portal, title, fields, true);
        survey.getFields().forEach(field -> field.setSurvey(survey));

        // TODO guardar survey, crear dao
        CreateSurveyResponse createSurveyResponse = surveysService.createSurvey(new CreateSurveyRequest(survey,
                                                                                                        portalId));

        return ok(createSurveyResponse.isOk() + survey.getTitle());
    }

    private void createField(List<Field> fields, JsonNode field) {
        final long fieldId = field.findValue("id").asLong();
        final String fieldType = field.findValue("type").asText();
        final JsonNode fieldConfigValue = field.findValue("config");

        final String key = fieldConfigValue.findValue("key").asText();
        final String label = fieldConfigValue.findValue("label").asText();
        final int order = fieldConfigValue.findValue("order").asInt();
        FieldConfig fieldConfig;

        switch (fieldType) {
            case FieldConfig.FieldConfigTypes.Textbox:
                fieldConfig = createTextboxConfig(fieldConfigValue, key, label, order);
                break;
            case FieldConfig.FieldConfigTypes.Rating:
                fieldConfig = createRatingFieldConfig(fieldConfigValue, key, label, order);
                break;
            case FieldConfig.FieldConfigTypes.Radio:
            case FieldConfig.FieldConfigTypes.Checkbox:
                fieldConfig = createRadioFieldConfig(fieldConfigValue, key, label, order);
                break;
            default:
                throw new RuntimeException("Error parsing received survey, unknown fieldType: " + fieldType);
        }

        fields.add(new Field(fieldId, fieldType, fieldConfig));
    }

    private FieldConfig createTextboxConfig(JsonNode fieldConfigValue, String key, String label, int order) {
        FieldConfig fieldConfig;
        boolean required = Optional.ofNullable(fieldConfigValue.findValue("required")).map(JsonNode::asBoolean).orElse(
                false);
        String value = Optional.ofNullable(fieldConfigValue.findValue("value")).map(JsonNode::asText).orElse(null);
        fieldConfig = new TextboxFieldConfig(key, label, required, order, value);
        return fieldConfig;
    }

    private FieldConfig createRatingFieldConfig(JsonNode fieldConfigValue, String key, String label, int order) {
        FieldConfig fieldConfig;
        int maximum = fieldConfigValue.findValue("maximum").asInt();
        fieldConfig = new RatingFieldConfig(key, label, order, maximum);
        return fieldConfig;
    }

    private FieldConfig createRadioFieldConfig(JsonNode fieldConfigValue, String key, String label, int order) {
        FieldConfig fieldConfig;
        final List<Option> options = new ArrayList<>();
        fieldConfigValue.withArray("options")
                        .elements()
                        .forEachRemaining(optionsNode -> createOption(options, optionsNode));
        fieldConfig = new RadioFieldConfig(key, label, order, options);
        return fieldConfig;
    }

    private void createOption(List<Option> options, JsonNode optionsNode) {
        Integer optionIndex = optionsNode.findValue("index").asInt();
        String optionKey = optionsNode.findValue("key").asText();
        options.add(new Option(optionIndex, optionKey));
    }

    private List<AnswersJson> buildAnswersJson(List<SummarizedAnswers> summarizedAnswers) {
        return summarizedAnswers.stream().map(x -> {
            List<String> labels = new ArrayList<>();
            List<Long> values = new ArrayList<>();

            if (FieldConfig.FieldConfigTypes.Rating.equals(x.type)) {
                x.answers.entrySet()
                         .stream()
                         .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())))
                         .forEachOrdered(entry -> {
                             labels.add(entry.getKey());
                             values.add(entry.getValue());
                         });
            } else {
                x.answers.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(entry -> {
                    labels.add(entry.getKey());
                    values.add(entry.getValue());
                });
            }

            return new AnswersJson(x.question, x.id, x.type, x.order, labels, values);
        }).collect(toList());
    }

    public static class DataJson {
        public List<AnswersJson> data;

        public DataJson() {
        }

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
        Stream<Field> fields = answeredSurvey.getFields()
                                             .stream()
                                             .sorted(Comparator.comparing(field -> field.getConfig().getOrder()));
        Stream<FieldAnswer> answers = userAnswers.stream().sorted(Comparator.comparing(answer -> answer.getField()
                                                                                                       .getConfig()
                                                                                                       .getOrder()));
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
