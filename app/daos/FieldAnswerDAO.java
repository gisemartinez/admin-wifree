package daos;

import com.google.common.collect.ImmutableListMultimap;
import io.ebean.Expr;
import models.FieldAnswer;
import models.NetworkUser;

import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

public class FieldAnswerDAO extends GenericDAO<FieldAnswer> {

    public FieldAnswerDAO() {
        super(FieldAnswer.class);
    }

    @SuppressWarnings("UnstableApiUsage")
    public ImmutableListMultimap<NetworkUser, FieldAnswer> findForSurvey(Long surveyId) {
        List<FieldAnswer> fieldAnswers = listWhere(Expr.eq("field.survey.id", surveyId));

        return fieldAnswers.stream().collect(toImmutableListMultimap(FieldAnswer::getUser, Function.identity()));
    }

}
