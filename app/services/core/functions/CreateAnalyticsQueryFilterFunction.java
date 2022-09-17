package services.core.functions;

import daos.AnalyticsQueryFilterDAO;
import models.AnalyticsQueryFilter;
import operations.requests.CreateAnalyticsQueryFilterRequest;
import operations.responses.CreateAnalyticsQueryFilterResponse;
import services.core.ServiceType;
import services.core.WiFreeFunction;

import java.util.function.Function;

@SuppressWarnings("unused")
public class CreateAnalyticsQueryFilterFunction extends WiFreeFunction<CreateAnalyticsQueryFilterRequest, CreateAnalyticsQueryFilterResponse> {

    @Override
    public Function<CreateAnalyticsQueryFilterRequest, CreateAnalyticsQueryFilterResponse> function() {
        function = request -> {
            final AnalyticsQueryFilter analyticsQueryFilter = request.queryFilter();
            final AnalyticsQueryFilterDAO analyticsQueryFilterDAO = new AnalyticsQueryFilterDAO();
            analyticsQueryFilterDAO.save(analyticsQueryFilter);
            return new CreateAnalyticsQueryFilterResponse(analyticsQueryFilter, true, null); // TODO check
        };
        return function;
    }

    @Override
    public Class<CreateAnalyticsQueryFilterRequest> rqClass() {
        return CreateAnalyticsQueryFilterRequest.class;
    }

    @Override
    public Class<CreateAnalyticsQueryFilterResponse> rsClass() {
        return CreateAnalyticsQueryFilterResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }

}
