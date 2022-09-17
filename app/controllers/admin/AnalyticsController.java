package controllers.admin;

import controllers.WiFreeController;
import models.AnalyticsQueryFilter;
import operations.requests.CreateAnalyticsQueryFilterRequest;
import operations.responses.CreateAnalyticsQueryFilterResponse;
import play.data.Form;
import play.mvc.Result;
import services.AnalyticsService;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class AnalyticsController extends WiFreeController {

    @Inject
    private AnalyticsService analyticsService;

    public CompletionStage<Result> createAnalyticsQuery() {
        final Form<AnalyticsQueryFilter> form = formFactory.form(AnalyticsQueryFilter.class);
        AnalyticsQueryFilter queryFilter = form.bindFromRequest().get();

        final CreateAnalyticsQueryFilterRequest request = new CreateAnalyticsQueryFilterRequest(queryFilter);
        final CompletionStage<CreateAnalyticsQueryFilterResponse> futureResponse = analyticsService.createAnalyticsQueryFilter(request);

        // TODO revisar
        return futureResponse.thenApplyAsync(response -> {
            if (response.isOk()) return ok();
            else return badRequest();
        });
    }
}
