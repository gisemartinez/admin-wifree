package services.core.functions;

import daos.NetworkUserConnectionLogDAO;
import models.NetworkUserConnectionLog;
import operations.requests.GetAnalyticsDataRequest;
import operations.responses.*;
import scala.Tuple2;
import services.core.*;
import utils.DateHelper;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static models.types.Gender.Female;
import static models.types.Gender.Male;
import static services.core.AnyRange.toRange;
import static services.core.HourRange.*;

@SuppressWarnings("unused")
public class GetAnalyticsDataFunction
        extends WiFreeFunction<GetAnalyticsDataRequest, GetAnalyticsDataResponse>
        implements CanGroupLogs {

    @Override
    public Function<GetAnalyticsDataRequest, GetAnalyticsDataResponse> function() {
        function = request -> {
            long portalId = request.portalId();
            Instant now = request.now();
            NetworkUserConnectionLogDAO dao = new NetworkUserConnectionLogDAO();
            DatasetFilter usersConnectedLastYearFilter = DatasetFilter.usersConnectedLastYearFilter(portalId);
            DatasetFilter maleUsersConnectedLastYearFilter = DatasetFilter.genderUsersConnectedLastYearFilter(portalId, Male);
            DatasetFilter femaleUsersConnectedLastYearFilter = DatasetFilter.genderUsersConnectedLastYearFilter(portalId, Female);
            DatasetFilter usersConnectedLastWeekFilter = DatasetFilter.usersConnectedLastWeekFilter(portalId);

            List<NetworkUserConnectionLog> usersConnectedLastYearLogs = dao.findForFilter(usersConnectedLastYearFilter, now);
            List<NetworkUserConnectionLog> maleUsersConnectedLastYearLogs = dao.findForFilter(maleUsersConnectedLastYearFilter, now);
            List<NetworkUserConnectionLog> femaleUsersConnectedLastYearLogs = dao.findForFilter(femaleUsersConnectedLastYearFilter, now);
            List<NetworkUserConnectionLog> usersConnectedLastWeekLogs = dao.findForFilter(usersConnectedLastWeekFilter, now);

            List<VisitsByPeriod> visitsByMonth = visitsByMonth(usersConnectedLastYearLogs);
            VisitsByPeriodByGender visitsByMonthByGender = visitsByPeriodByGender(maleUsersConnectedLastYearLogs, femaleUsersConnectedLastYearLogs);
            VisitsByDayByTimeRange averageVisitsByDayByTimeRange = averageVisitsByDayByTimeRange(usersConnectedLastYearLogs);
            Map<Tuple2<Integer, Integer>, List<VisitsByPeriod>> visitsByDurationLastWeek = visitsByDurationLastWeek(usersConnectedLastWeekLogs);

            return new GetAnalyticsDataResponse(visitsByMonth,
                    visitsByMonthByGender,
                    averageVisitsByDayByTimeRange,
                    visitsByDurationLastWeek);
        };
        return function;
    }

    private List<VisitsByPeriod> visitsByMonth(List<NetworkUserConnectionLog> usersConnectedLastYearLogs) {
        Map<String, Long> visitsByMonthMap = groupCounting(
                usersConnectedLastYearLogs,
                NetworkUserConnectionLog::getConnectionStartDate,
                this::instantToYearMonth
        );
        ArrayList<String> months = new ArrayList<>(visitsByMonthMap.keySet());
        months.sort(Comparator.naturalOrder());
        return months.stream()
                .map(month -> new VisitsByPeriod(month, visitsByMonthMap.get(month)))
                .collect(Collectors.toList());
    }

    private VisitsByPeriodByGender visitsByPeriodByGender(List<NetworkUserConnectionLog> maleUsersConnectedLastYearLogs, List<NetworkUserConnectionLog> femaleUsersConnectedLastYearLogs) {
        List<VisitsByPeriod> maleVisitsByPeriod = visitsByMonth(maleUsersConnectedLastYearLogs);
        List<VisitsByPeriod> femaleVisitsByPeriod = visitsByMonth(femaleUsersConnectedLastYearLogs);
        return new VisitsByPeriodByGender(maleVisitsByPeriod, femaleVisitsByPeriod);
    }

    private VisitsByDayByTimeRange averageVisitsByDayByTimeRange(List<NetworkUserConnectionLog> usersConnectedLastYearLogs) {
        Map<DayAndRange, Long> dayAndRangeVisits = groupCounting(
                usersConnectedLastYearLogs,
                NetworkUserConnectionLog::getConnectionStartDate,
                date -> new DayAndRange(DateHelper.dayOfWeek(date), toRange(HourRange.values, DateHelper.hourBeginning(date)))
        );
        return new VisitsByDayByTimeRange(
                averageVisitsByDayByRange(dayAndRangeVisits, RANGE_0_TO_8),
                averageVisitsByDayByRange(dayAndRangeVisits, RANGE_8_TO_11),
                averageVisitsByDayByRange(dayAndRangeVisits, RANGE_11_TO_13),
                averageVisitsByDayByRange(dayAndRangeVisits, RANGE_13_TO_16),
                averageVisitsByDayByRange(dayAndRangeVisits, RANGE_16_TO_20),
                averageVisitsByDayByRange(dayAndRangeVisits, RANGE_20_TO_24)
        );
    }

    private List<VisitsByPeriod> averageVisitsByDayByRange(Map<DayAndRange, Long> dayAndRangeVisits, Tuple2<Integer, Integer> range) {
        return dayAndRangeVisits.entrySet()
                .stream()
                .filter(entry -> entry.getKey().timeRange.equals(range))
                .collect(
                        Collectors.groupingBy(
                                entry -> entry.getKey().dayOfWeek,
                                Collectors.counting()
                        )
                )
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> new VisitsByPeriod(entry.getKey().name(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Map<Tuple2<Integer, Integer>, List<VisitsByPeriod>> visitsByDurationLastWeek(List<NetworkUserConnectionLog> usersConnectedLastWeekLogs) {
        Map<Tuple2<Integer, Integer>, List<NetworkUserConnectionLog>> logsByTimeRanges =
                groupMapping(
                        usersConnectedLastWeekLogs,
                        this::logToMinutesRange,
                        l -> l
                );

        Map<Tuple2<Integer, Integer>, Map<String, List<NetworkUserConnectionLog>>> logsByDatesByTimeRanges = logsByTimeRanges.entrySet()
                .stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> groupMapping(entry.getValue(),
                                        this::logToYearMonthDay,
                                        l -> l)
                        )
                );
        Map<Tuple2<Integer, Integer>, List<VisitsByPeriod>> visitsByDurationLastWeek = logsByDatesByTimeRanges.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .entrySet()
                                .stream()
                                .map(x -> new VisitsByPeriod(x.getKey(), x.getValue().size()))
                                .collect(Collectors.toList())));

        List<String> allPeriods = visitsByDurationLastWeek.values()
                .stream()
                .flatMap(Collection::stream)
                .map(VisitsByPeriod::period)
                .distinct()
                .collect(Collectors.toList());

        allPeriods.forEach(period -> {
            for (Map.Entry<Tuple2<Integer, Integer>, List<VisitsByPeriod>> entry : visitsByDurationLastWeek.entrySet()) {
                List<VisitsByPeriod> values = entry.getValue();
                if (values.stream().noneMatch(v -> v.period().equals(period))) {
                    values.add(new VisitsByPeriod(period, 0));
                }
                values.sort(Comparator.comparing(VisitsByPeriod::period));
                entry.setValue(takeLastWeek(values));
            }
        });

        return visitsByDurationLastWeek;
    }

    private List<VisitsByPeriod> takeLastWeek(List<VisitsByPeriod> list) {
        int size = list.size();
        return list.subList(Math.max(size - 7, 0), size);
    }

    private String logToYearMonthDay(NetworkUserConnectionLog l) {
        return l.getConnectionStartDate().toString().split("T")[0].substring(0, 10);
    }

    private Tuple2<Integer, Integer> logToMinutesRange(NetworkUserConnectionLog l) {
        return toRange(MinutesRange.values, (int) (DateHelper.between(l.getConnectionEndDate(), l.getConnectionStartDate()) / 60));
    }

    private String instantToYearMonth(Instant date) {
        return date.toString().split("T")[0].substring(0, 7);
    }

    @Override
    public Class<GetAnalyticsDataRequest> rqClass() {
        return GetAnalyticsDataRequest.class;
    }

    @Override
    public Class<GetAnalyticsDataResponse> rsClass() {
        return GetAnalyticsDataResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }

    static class DayAndRange {
        final DayOfWeek dayOfWeek;
        final Tuple2<Integer, Integer> timeRange;

        DayAndRange(final DayOfWeek dayOfWeek, final Tuple2<Integer, Integer> timeRange) {
            this.dayOfWeek = dayOfWeek;
            this.timeRange = timeRange;
        }
    }

}
