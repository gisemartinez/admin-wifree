package services.core.functions;

import operations.requests.PortalNetworkConfigurationRequest;
import operations.responses.PortalNetworkConfigurationResponse;
import services.core.ServiceType;
import services.core.WiFreeFunction;

import java.util.function.Function;

@SuppressWarnings("unused")
public class PortalNetworkConfigurationFunction extends WiFreeFunction<PortalNetworkConfigurationRequest, PortalNetworkConfigurationResponse> {

    @Override
    public Function<PortalNetworkConfigurationRequest, PortalNetworkConfigurationResponse> function() {
        function = request -> new PortalNetworkConfigurationResponse(request.name() + " and Charles");
        return function;
    }

    @Override
    public Class<PortalNetworkConfigurationRequest> rqClass() {
        return PortalNetworkConfigurationRequest.class;
    }

    @Override
    public Class<PortalNetworkConfigurationResponse> rsClass() {
        return PortalNetworkConfigurationResponse.class;
    }

    @Override
    public ServiceType serviceType() {
        return ServiceType.TESTING_SERVICE;
    }

}
