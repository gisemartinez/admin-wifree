package services.core;

import operations.core.WiFreeRequest;
import operations.core.WiFreeResponse;

import java.util.function.Function;

public abstract class WiFreeFunction<RQ extends WiFreeRequest, RS extends WiFreeResponse> {

    protected Function<RQ, RS> function;

    public abstract Function<RQ, RS> function();

    final public RS apply(RQ request) {
        return function().apply(request);
    }

    public abstract Class<RQ> rqClass();

    public abstract Class<RS> rsClass();

    public abstract ServiceType serviceType();

}
