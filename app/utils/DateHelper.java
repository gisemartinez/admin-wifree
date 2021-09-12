package utils;

import scala.Tuple2;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.function.Function;

public class DateHelper {
	
//	private static final ZoneId zone = ZoneId.systemDefault();
	private static final ZoneId zone = ZoneId.of("UTC");

	// Public methods

	public static DayOfWeek dayOfWeek(Instant date) {
		return toLocalDate(date).getDayOfWeek();
	}

	public static int hourBeginning(Instant date) {
		return date.atZone(zone).getHour();
	}

	public static Instant dayBeginning(Instant date) {
		return processInstant(localDate -> localDate.atStartOfDay(zone).toLocalDate(), date);
	}

	public static Instant weekBeginning(Instant date) {
		return processInstant(localDate -> localDate.with(DayOfWeek.MONDAY), date);
	}

	public static Instant monthBeginning(Instant date) {
		return processInstant(localDate -> localDate.withDayOfMonth(1), date);
	}

	public static Instant yearBeginning(Instant date) {
		return processInstant(localDate -> localDate.withDayOfYear(1), date);
	}

	public static Instant oneDayAgo(Instant date) {
		return processInstant(localDate -> localDate.minusDays(1), date);
	}

	public static Instant fifteenMinutesAgo(Instant date) {
		return processInstantTime(localDate -> localDate.minus(Duration.ofMinutes(15)), date);
	}

	public static Instant oneWeekAgo(Instant date) {
		return processInstant(localDate -> localDate.minusWeeks(1), date);
	}

	public static Instant oneMonthAgo(Instant date) {
		return processInstant(localDate -> localDate.minusMonths(1), date);
	}

	public static Instant oneYearAgo(Instant date) {
		return processInstant(localDate -> localDate.minusYears(1), date);
	}

	public static Instant now() {
		return Instant.now();
	}

	public static Instant min() {
		return Instant.parse("1970-01-01T00:00:00.000Z");
	}

	public static Tuple2<Instant, Instant> strings2Dates(String dates) {
		final Tuple2<String, String> splittedDatesStrings = StringHelper.splitBlank(dates);
		return Tuple2.apply(Instant.parse(splittedDatesStrings._1()), Instant.parse(splittedDatesStrings._2()));
	}

	/**
	 *
	 * @param latestDate
	 * @param earlierDate
	 * @return The seconds between latestDate and earlierDate
	 */
	public static long between(Instant latestDate, Instant earlierDate) {
		return toLocalDateTime(earlierDate).until(toLocalDateTime(latestDate), ChronoUnit.SECONDS);
	}

	public static long yearsBetween(Instant latestDate, Instant earlierDate) {
		return toLocalDateTime(earlierDate).until(toLocalDateTime(latestDate), ChronoUnit.YEARS);
	}



	// Private methods

	private static Instant processInstant(Function<LocalDate, LocalDate> f, Instant instant) {
		return toInstant(f.apply(toLocalDate(instant)));
	}

	private static Instant processInstantTime(Function<LocalDateTime, LocalDateTime> f, Instant instant) {
		return toInstant(f.apply(toLocalDateTime(instant)));
	}

	private static LocalDate toLocalDate(Instant instant) {
		return instant.atZone(zone).toLocalDate();
	}

	private static LocalDateTime toLocalDateTime(Instant instant) {
		return instant.atZone(zone).toLocalDateTime();
	}

	private static Instant toInstant(LocalDate localDate) {
		return localDate.atStartOfDay(zone).toInstant();
	}

	private static Instant toInstant(LocalDateTime localDateTime) {
		return localDateTime.atZone(zone).toInstant();
	}
}
