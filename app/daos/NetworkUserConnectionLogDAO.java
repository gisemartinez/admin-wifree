package daos;

import io.ebean.Ebean;
import io.ebean.Expr;
import io.ebean.Expression;
import io.ebean.ExpressionList;
import models.NetworkUserConnectionLog;
import operations.responses.BuiltExpressions;
import operations.responses.DatasetFilter;
import scala.collection.JavaConverters;

import java.time.Instant;
import java.util.List;

public class NetworkUserConnectionLogDAO extends GenericDAO<NetworkUserConnectionLog> {

    public NetworkUserConnectionLogDAO() {
        super(NetworkUserConnectionLog.class);
    }

    public List<NetworkUserConnectionLog> findForFilter(DatasetFilter filter, Instant now) {
        final ExpressionList<NetworkUserConnectionLog> whereExpressions = Ebean.createQuery(NetworkUserConnectionLog.class).where();
        final BuiltExpressions expressions = filter.expressions();
        expressions.timePeriodExpression().foreach(e -> whereExpressions.add(e.apply("connectionStartDate", now)));
        expressions.visitsAmountQuery().expression().foreach(whereExpressions::add);
        for (Expression e : JavaConverters.seqAsJavaList(expressions.parameterlessExpressions())) {
            whereExpressions.add(e);
        }

        return Ebean.createQuery(NetworkUserConnectionLog.class)
                .select("*")
                .where()
                .addAll(whereExpressions)
                .findList();
    }

    public List<NetworkUserConnectionLog> listForPortal(Long portalId) {
        Expression portalExpression = Expr.eq("portal.id", portalId);
        return listWhere(portalExpression);
    }

}
