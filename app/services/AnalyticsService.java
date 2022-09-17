package services;

import operations.requests.*;
import operations.responses.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AnalyticsService extends WiFreeService {

    public CompletionStage<CreateAnalyticsQueryFilterResponse> createAnalyticsQueryFilter(CreateAnalyticsQueryFilterRequest request) {
        return CompletableFuture.supplyAsync(() -> processRequest(request));
    }

    public CompletionStage<GetAnalyticsHomeDatasetsResponse> getAnalyticsHomeDatasets(GetAnalyticsHomeDatasetsRequest request) {
        return CompletableFuture.supplyAsync(() -> processRequest(request));
    }

    public CompletionStage<RunAnalyticsQueryFilterResponse> runAnalyticsQueryFilter(RunAnalyticsQueryFilterRequest request) {
        return CompletableFuture.supplyAsync(() -> processRequest(request));
    }

    public GetDashboardDataResponse getDashboardData(GetDashboardDataRequest getDashboardDataRequest) {
        return processRequest(getDashboardDataRequest);
    }

    public GetAnalyticsDataResponse getAnalyticsData(GetAnalyticsDataRequest request) {
        return processRequest(request);
    }
}
