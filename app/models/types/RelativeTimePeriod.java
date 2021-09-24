package models.types;

import io.ebean.Expr;
import io.ebean.Expression;
import io.ebean.annotation.EnumValue;
import utils.DateHelper;

import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;


public enum RelativeTimePeriod {

	@EnumValue("LAST_FIFTEEN_MINUTES") LAST_FIFTEEN_MINUTES(buildExpression(DateHelper::sixtyMinutesAgo)),
	@EnumValue("LAST_DAY") LAST_DAY(buildExpression(DateHelper::oneDayAgo)),
	@EnumValue("LAST_WEEK") LAST_WEEK(buildExpression(DateHelper::oneWeekAgo)),
	@EnumValue("LAST_MONTH") LAST_MONTH(buildExpression(DateHelper::oneMonthAgo)),
	@EnumValue("LAST_YEAR") LAST_YEAR(buildExpression(DateHelper::oneYearAgo)),
	@EnumValue("TODAY") TODAY(buildExpression(DateHelper::dayBeginning)),
	@EnumValue("THIS_WEEK") THIS_WEEK(buildExpression(DateHelper::weekBeginning)),
	@EnumValue("THIS_MONTH") THIS_MONTH(buildExpression(DateHelper::monthBeginning)),
	@EnumValue("THIS_YEAR") THIS_YEAR(buildExpression(DateHelper::yearBeginning)),
	@EnumValue("BEFORE_THIS_WEEK") BEFORE_THIS_WEEK(buildInvertedExpression(DateHelper::weekBeginning)),
	@EnumValue("BEFORE_LAST_WEEK") BEFORE_LAST_WEEK(buildInvertedExpression(DateHelper::oneWeekAgo)),
	@EnumValue("ERROR") ERROR((String date, Instant now) -> { throw new IllegalArgumentException("There is no valid Expression value for ERROR"); } );

	private final BiFunction<String, Instant, Expression> expr;

	RelativeTimePeriod(BiFunction<String, Instant, Expression> expressionFunction) {
		this.expr = expressionFunction;
	}
	
	public BiFunction<String, Instant, Expression> getExpression() {
		return expr;
	}
	
	private static BiFunction<String, Instant, Expression> buildExpression(UnaryOperator<Instant> operator) {
		return (String propertyName, Instant now) -> between(propertyName, operator, now);
	}

	private static BiFunction<String, Instant, Expression> buildInvertedExpression(UnaryOperator<Instant> operator) {
		return (String propertyName, Instant now) -> betweenInverted(propertyName, operator, now);
	}

	private static Expression between(String propertyName, UnaryOperator<Instant> operator, Instant now) {
		return Expr.between(propertyName, operator.apply(now), now);
	}

	private static Expression betweenInverted(String propertyName, UnaryOperator<Instant> operator, Instant now) {
		Instant min = DateHelper.min();
		return Expr.between(propertyName, min, operator.apply(now));
	}

}
