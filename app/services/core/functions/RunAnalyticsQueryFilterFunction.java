package services.core.functions;

import models.AnalyticsQueryFilter;
import operations.requests.RunAnalyticsQueryFilterRequest;
import operations.responses.RunAnalyticsQueryFilterResponse;
import services.core.ServiceType;
import services.core.WiFreeFunction;

import java.util.function.Function;

@SuppressWarnings("unused")
public class RunAnalyticsQueryFilterFunction extends WiFreeFunction<RunAnalyticsQueryFilterRequest, RunAnalyticsQueryFilterResponse> {

    @Override
    public Function<RunAnalyticsQueryFilterRequest, RunAnalyticsQueryFilterResponse> function() {
        function = request -> {
            final AnalyticsQueryFilter analyticsQueryFilter = request.queryFilter();
            // TimePeriod: factory con parametros timePeriodType y timePeriod, que cree objeto que sepa consultar(?) esa consulta, o, que sepa crear la query
            // Gender: si es valido, es =, sino ignorar / error(?)

            return null;
        };
        return function;
    }

    @Override
    public Class<RunAnalyticsQueryFilterRequest> rqClass() {
        return RunAnalyticsQueryFilterRequest.class;
    }

    @Override
    public Class<RunAnalyticsQueryFilterResponse> rsClass() {
        return RunAnalyticsQueryFilterResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }

}
