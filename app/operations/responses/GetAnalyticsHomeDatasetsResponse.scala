package operations.responses

import io.ebean.{Ebean, Expr, Expression, SqlQuery}
import models.types.{DayOfTheWeek, Gender, RelativeTimePeriod, TimePeriodType}
import operations.core.{ResponseType, WiFreeResponse}
import utils.DateHelper

import java.util.function.{Function => JFunction}
import java.util.function.BiFunction
import java.lang.{Long => JLong}
import java.time.Instant
import models.types.RelativeTimePeriod.{BEFORE_LAST_WEEK, LAST_FIFTEEN_MINUTES, LAST_WEEK, LAST_YEAR}
import models.types.TimePeriodType.{ABSOLUTE, NONE, RELATIVE}

import scala.collection.JavaConverters._
import utils.ScalaHelper._

import scala.collection.JavaConverters

case class GetAnalyticsHomeDatasetsResponse(datasetFilters: Seq[DatasetFilter]) extends WiFreeResponse {

	override val success: Boolean = ???
	override val errors: List[String] = ???

	override def responseType: ResponseType = ResponseType.GET_ANALYTICS_HOME_DATASETS_RESPONSE

	override def isOk(): Boolean = ???

}

case class DatasetFilter(portalId: Long,
												 name: String,
												 description: String,
												 timePeriod: String,
												 timePeriodType: TimePeriodType,
												 gender: Option[Gender],
												 age: Option[MinMax[Int]],
												 visitsAmount: Option[MinMax[Int]],
												 visitorAddress: Option[String],
												 daysOfTheWeek: Option[Seq[DayOfTheWeek]],
												 hours: Option[(String, String)]
												) {
	
	def timePeriodExpression: Option[BiFunction[String, Instant, Expression]] = timePeriodType match {
		case ABSOLUTE => val (startDate, endDate) = DateHelper.strings2Dates(timePeriod)
			Option((propertyName: String, _: Instant) => Expr.between(propertyName, startDate, endDate))
		case RELATIVE => Option(RelativeTimePeriod.valueOf(timePeriod).getExpression)
		case NONE => None
		case u => throw new Exception(s"Unknown timePeriodType: $u")
	}
	
	def expressions: BuiltExpressions = {
		val portalExpression = Option(Expr.eq("portal.id", portalId))
		val genderExpression = gender.map(g => Expr.eq("networkUser.gender", g.toString))
		val ageExpression = age.map(a => a.expr("networkUser.age"))
		val visitsAmountQuery = VisitsAmountQuery(visitsAmount.map { mm =>
			Ebean.createSqlQuery("SELECT network_user_id"
					+ "FROM (SELECT network_user_id, count(1) AS n FROM network_user_connection_log GROUP BY network_user_id) AS x"
					+ "WHERE x.n >= :min AND x.n <= :max")
					.setParameter("min", mm.min)
					.setParameter("max", mm.max)
		})
		val visitorAddressExpression = visitorAddress.map { va =>
			val s = s"%$va%"
			Expr.or(
				Expr.ilike("address.street", s),
				Expr.or(
					Expr.ilike("address.other", s),
					Expr.or(
						Expr.ilike("address.city", s),
						Expr.or(
							Expr.ilike("address.state", s),
							Expr.ilike("address.country", s)
						)
					)
				)
			)
		}
		val daysOfTheWeekExpression = daysOfTheWeek.map(ds => Expr.raw(s"dow(connection_start_date) in (?)", ds.map(_.ordinal()).mkString(", ")))
		val hoursExpression = hours.map { case (hMin, hMax) => Expr.raw("connection_start_date::time between ? AND ?", Array[Object](hMin, hMax)) }
		val parameterlessExpressions = Seq(
			portalExpression,
			genderExpression,
			ageExpression,
			visitorAddressExpression,
			daysOfTheWeekExpression,
			hoursExpression
		).flatten
		BuiltExpressions(timePeriodExpression, visitsAmountQuery, parameterlessExpressions)
	}
}

object DatasetFilter {

	def usersConnectedLastWeekFilter(portalId: Long): DatasetFilter = relativeTimePeriodFilter(portalId, LAST_WEEK)

	def usersConnectedBeforeLastWeekFilter(portalId: Long): DatasetFilter = relativeTimePeriodFilter(portalId, BEFORE_LAST_WEEK)

	def usersConnectedLastYearFilter(portalId: Long): DatasetFilter = relativeTimePeriodFilter(portalId, LAST_YEAR)

	def genderUsersConnectedLastYearFilter(portalId: Long, gender: Gender): DatasetFilter = relativeTimePeriodFilter(portalId, LAST_YEAR, Option(gender))

	def usersConnectedLastFifteenMinutesFilter(portalId: Long): DatasetFilter = relativeTimePeriodFilter(portalId, LAST_FIFTEEN_MINUTES)

	private def relativeTimePeriodFilter(portalId: Long, relativeTimePeriod: RelativeTimePeriod, gender: Option[Gender] = None) = DatasetFilter(
		portalId = portalId,
		name = "",
		description = "",
		timePeriod = relativeTimePeriod.toString,
		timePeriodType = TimePeriodType.RELATIVE,
		gender = gender,
		age = None,
		visitsAmount = None,
		visitorAddress = None,
		daysOfTheWeek = None,
		hours = None
	)

}

case class MinMax[T](min: T, max: T) {
	def expr: String => Expression = (propertyName: String) => Expr.between(propertyName, min, max)
}

case class BuiltExpressions(timePeriodExpression: Option[BiFunction[String, Instant, Expression]],
														visitsAmountQuery: VisitsAmountQuery,
														parameterlessExpressions: Seq[Expression])

case class VisitsAmountQuery(maybeSqlQuery: Option[SqlQuery]) {
	private def build: Option[List[JLong]] = maybeSqlQuery.map { sqlQuery =>
		sqlQuery.findList().asScala.map(row => row.getLong("network_user_id")).toList
	}
	def expression: Option[Expression] = build.map(ids => Expr.in("networkUser.id", JavaConverters.asJavaCollection(ids)))
}