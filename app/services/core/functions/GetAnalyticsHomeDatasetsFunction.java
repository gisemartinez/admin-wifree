package services.core.functions;

import operations.requests.GetAnalyticsHomeDatasetsRequest;
import operations.responses.GetAnalyticsHomeDatasetsResponse;
import services.core.ServiceType;
import services.core.WiFreeFunction;

import java.util.function.Function;

@SuppressWarnings("unused")
public class GetAnalyticsHomeDatasetsFunction extends WiFreeFunction<GetAnalyticsHomeDatasetsRequest, GetAnalyticsHomeDatasetsResponse> {

    @Override
    public Function<GetAnalyticsHomeDatasetsRequest, GetAnalyticsHomeDatasetsResponse> function() {
        function = request -> {
            return null;
        };
        return function;
    }

    @Override
    public Class<GetAnalyticsHomeDatasetsRequest> rqClass() {
        return GetAnalyticsHomeDatasetsRequest.class;
    }

    @Override
    public Class<GetAnalyticsHomeDatasetsResponse> rsClass() {
        return GetAnalyticsHomeDatasetsResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }

}
