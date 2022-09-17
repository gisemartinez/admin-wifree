package services.core.functions;

import daos.NetworkUserConnectionLogDAO;
import models.BaseModel;
import models.NetworkUser;
import models.NetworkUserConnectionLog;
import models.types.Gender;
import operations.requests.GetDashboardDataRequest;
import operations.responses.DatasetFilter;
import operations.responses.GetDashboardDataResponse;
import scala.Tuple2;
import services.core.AgeRange;
import services.core.CanGroupLogs;
import services.core.ServiceType;
import services.core.WiFreeFunction;
import utils.DateHelper;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static services.core.AnyRange.toRange;
import static utils.DateHelper.between;

@SuppressWarnings("unused")
public class GetDashboardDataFunction
        extends WiFreeFunction<GetDashboardDataRequest, GetDashboardDataResponse>
        implements CanGroupLogs {

    @Override
    public Function<GetDashboardDataRequest, GetDashboardDataResponse> function() {
        function = request -> {
            NetworkUserConnectionLogDAO connectionLogDAO = new NetworkUserConnectionLogDAO();
            long portalId = request.portalId();
            Instant now = request.now();

            DatasetFilter lastWeekFilter = DatasetFilter.usersConnectedLastWeekFilter(portalId);
            DatasetFilter beforeLastWeekFilter = DatasetFilter.usersConnectedBeforeLastWeekFilter(portalId);
            List<NetworkUserConnectionLog> logsLastWeek = connectionLogDAO.findForFilter(lastWeekFilter, now);
            List<NetworkUserConnectionLog> logsBeforeLastWeek = connectionLogDAO.findForFilter(beforeLastWeekFilter, now);
            Supplier<Stream<NetworkUser>> usersConnectedLastWeekSupplier = usersConnectedLastWeekSupplier(logsLastWeek);

            Long amountOfUsersConnectedLastWeek = amountOfUsersConnectedLastWeek(usersConnectedLastWeekSupplier);

            Map<Tuple2<Integer, Integer>, Long> usersByAgeRangeLastWeek = usersByAgeRangeLastWeek(logsLastWeek);

            long amountOfNewUsersLastWeek = amountOfNewUsersLastWeek(logsBeforeLastWeek, usersConnectedLastWeekSupplier);

            Map<Gender, Long> usersByGenderLastWeek = usersByGenderLastWeek(logsLastWeek);

            String busiestDayLastWeek = busiestDayLastWeek(logsLastWeek);

            String busiestTimeLastWeek = busiestTimeLastWeek(logsLastWeek);

            double usersLoyalty = usersLoyalty(logsLastWeek, logsBeforeLastWeek);

            long usersOnline = usersOnline(logsLastWeek, now);

            return new GetDashboardDataResponse(
                    amountOfUsersConnectedLastWeek,
                    usersByAgeRangeLastWeek,
                    amountOfNewUsersLastWeek,
                    usersByGenderLastWeek,
                    busiestDayLastWeek,
                    busiestTimeLastWeek,
                    usersLoyalty,
                    usersOnline
            );
        };
        return function;
    }

    private double usersLoyalty(List<NetworkUserConnectionLog> logsLastWeek, List<NetworkUserConnectionLog> logsBeforeLastWeek) {
        Set<NetworkUser> usersLastWeek = logsLastWeek.stream()
                .map(NetworkUserConnectionLog::getNetworkUser)
                .collect(Collectors.toSet());
        Set<NetworkUser> usersBeforeLastWeek = logsBeforeLastWeek.stream()
                .map(NetworkUserConnectionLog::getNetworkUser)
                .collect(Collectors.toSet());
        double amountOfAlreadyRegisteredUsersThatReturnedThisLastWeek = usersLastWeek.stream()
                .filter(usersBeforeLastWeek::contains)
                .count();
        double amountOfUsersConnectedBeforeLastWeek = usersBeforeLastWeek.size();
        return amountOfAlreadyRegisteredUsersThatReturnedThisLastWeek / amountOfUsersConnectedBeforeLastWeek;
    }

    private String busiestTimeLastWeek(List<NetworkUserConnectionLog> logsLastWeek) {
        Map<Integer, Long> visitsPerHourLastWeek = groupCounting(logsLastWeek,
                NetworkUserConnectionLog::getConnectionStartDate,
                DateHelper::hourBeginning);
        int hour = visitsPerHourLastWeek.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .get()
                .getKey();
        return String.format("%02d:00", hour);
    }

    private String busiestDayLastWeek(List<NetworkUserConnectionLog> logsLastWeek) {
        Map<Instant, Long> visitsByDayLastWeek = groupCounting(
                logsLastWeek,
                NetworkUserConnectionLog::getConnectionStartDate,
                DateHelper::dayBeginning
        );
        return visitsByDayLastWeek.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .get()
                .getKey()
                .toString()
                .split("T")[0];
    }

    private Map<Gender, Long> usersByGenderLastWeek(List<NetworkUserConnectionLog> logsThisWeek) {
        return groupDistinctCounting(logsThisWeek,
                NetworkUserConnectionLog::getNetworkUser,
                NetworkUser::getGender);
    }

    private long amountOfNewUsersLastWeek(List<NetworkUserConnectionLog> logsBeforeThisWeek, Supplier<Stream<NetworkUser>> usersConnectedThisWeekSupplier) {
        Stream<NetworkUser> usersConnectedBeforeThisWeek = logsBeforeThisWeek.stream()
                .map(NetworkUserConnectionLog::getNetworkUser)
                .distinct();
        Set<Long> usersConnectedBeforeThisWeekIds = usersConnectedBeforeThisWeek
                .map(BaseModel::getId)
                .collect(Collectors.toSet());
        Stream<NetworkUser> newUsersThisWeek = usersConnectedThisWeekSupplier.get()
                .filter(u -> !usersConnectedBeforeThisWeekIds.contains(u.getId()));
        return newUsersThisWeek.count();
    }

    private Map<Tuple2<Integer, Integer>, Long> usersByAgeRangeLastWeek(List<NetworkUserConnectionLog> logsLastWeek) {
        return groupDistinctCounting(
                logsLastWeek,
                NetworkUserConnectionLog::getNetworkUser,
                user -> toRange(AgeRange.values, user.getAge())
        );
    }

    private Long amountOfUsersConnectedLastWeek(Supplier<Stream<NetworkUser>> usersConnectedThisWeekSupplier) {
        Stream<NetworkUser> usersConnectedThisWeek = usersConnectedThisWeekSupplier.get();
        return usersConnectedThisWeek.count();
    }

    private Supplier<Stream<NetworkUser>> usersConnectedLastWeekSupplier(List<NetworkUserConnectionLog> logsThisWeek) {
        return () -> logsThisWeek.stream()
                .map(NetworkUserConnectionLog::getNetworkUser)
                .distinct();
    }

    private long usersOnline(List<NetworkUserConnectionLog> logsLastWeek, Instant now) {
        long fiveMinutes = 60 * 5;
        return logsLastWeek.stream()
                .filter(l -> between(now, l.getConnectionStartDate()) <= fiveMinutes)
                .map(NetworkUserConnectionLog::getNetworkUser)
                .distinct()
                .count();
    }

    @Override
    public Class<GetDashboardDataRequest> rqClass() {
        return GetDashboardDataRequest.class;
    }

    @Override
    public Class<GetDashboardDataResponse> rsClass() {
        return GetDashboardDataResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }

}
