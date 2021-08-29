package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import daos.SurveyDAO;
import models.PortalNetworkConfiguration;
import models.Survey;
import operations.requests.GetAllSurveysRequest;
import operations.requests.GetAnalyticsDataRequest;
import operations.responses.*;
import org.pac4j.core.profile.CommonProfile;
import play.api.libs.json.JsValue;
import play.data.Form;
import play.mvc.Result;
import scala.Tuple2;
import services.AnalyticsService;
import services.ConnectionsService;
import services.SurveysService;
import services.core.MinutesRange;
import utils.DateHelper;
import utils.JsonHelper;
import views.dto.ConnectedUser;
import views.dto.SurveySummary;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jesu on 27/06/17.
 */
public class AdminAppController extends WiFreeController {

	@Inject
	AnalyticsService analyticsService;

	@Inject
	SurveysService surveysService;

	@Inject
	ConnectionsService connectionsService;

	@SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
	public Result dashboard() throws NoProfileFoundException {
		JsValue[] dashboardMockedData = AdminMockedData.dashboard();
		JsValue jsGenderGraphData = dashboardMockedData[0];
		JsValue jsAgeGraphData = dashboardMockedData[1];
		JsValue jsConnectionsGraphDataThisWeek = dashboardMockedData[2];
		JsValue jsConnectionsGraphDataLastWeek = dashboardMockedData[3];
		return ok(views.html.admin.dashboard.render(
				getCurrentProfile(),
				jsGenderGraphData,
				jsAgeGraphData,
				jsConnectionsGraphDataThisWeek,
				jsConnectionsGraphDataLastWeek)
		);
	}

	@SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
	public Result analytics() throws NoProfileFoundException {
		JsValue[] analyticsMockedData = AdminMockedData.analytics();
		JsValue jsValue15 = analyticsMockedData[0];
		JsValue jsValue16 = analyticsMockedData[1];
		JsValue jsValue17 = analyticsMockedData[2];
		JsValue jsValue18 = analyticsMockedData[3];

		CommonProfile currentProfile = getCurrentProfile();
		Long portalId = portalId();

		GetAnalyticsDataRequest analyticsRequest = new GetAnalyticsDataRequest(portalId, DateHelper.now());

		GetAnalyticsDataResponse analyticsData = analyticsService.getAnalyticsData(analyticsRequest);

		List<VisitsByPeriod> visitsByMonth = analyticsData.visitsByMonthLastYear();
		final JsValue visitsByMonthJson = JsonHelper.toJson(visitsByMonth);

		VisitsByPeriodByGender visitsByPeriodByGender = analyticsData.visitsByMonthLastYearByGender();
		List<VisitsByPeriod> visitsByPeriodMale = visitsByPeriodByGender.male();
		List<VisitsByPeriod> visitsByPeriodFemale = visitsByPeriodByGender.female();
		JsValue visitsByPeriodMaleJson = JsonHelper.toJson(visitsByPeriodMale);
		JsValue visitsByPeriodFemaleJson = JsonHelper.toJson(visitsByPeriodFemale);

		VisitsByDayByTimeRange visitsByDayByTimeRange = analyticsData.visitsByDayByTimeRange();
		List<VisitsByPeriod> visits0_8 = visitsByDayByTimeRange.visits0_8();
		List<VisitsByPeriod> visits8_11 = visitsByDayByTimeRange.visits8_11();
		List<VisitsByPeriod> visits11_13 = visitsByDayByTimeRange.visits11_13();
		List<VisitsByPeriod> visits13_16 = visitsByDayByTimeRange.visits13_16();
		List<VisitsByPeriod> visits16_20 = visitsByDayByTimeRange.visits16_20();
		List<VisitsByPeriod> visits20_24 = visitsByDayByTimeRange.visits20_24();
		JsValue visits0_8Json = JsonHelper.toJson(visits0_8);
		JsValue visits8_11Json = JsonHelper.toJson(visits8_11);
		JsValue visits11_13Json = JsonHelper.toJson(visits11_13);
		JsValue visits13_16Json = JsonHelper.toJson(visits13_16);
		JsValue visits16_20Json = JsonHelper.toJson(visits16_20);
		JsValue visits20_24Json = JsonHelper.toJson(visits20_24);

		List<VisitsByPeriod> emptyList = new ArrayList<>();
		Map<Tuple2<Integer, Integer>, List<VisitsByPeriod>> visitsByDurationLastWeek = analyticsData.visitsByDurationLastWeek();
		List<VisitsByPeriod> visits0to15 = visitsByDurationLastWeek.getOrDefault(MinutesRange.RANGE_0_TO_15, emptyList);
		List<VisitsByPeriod> visits15to30 = visitsByDurationLastWeek.getOrDefault(MinutesRange.RANGE_15_TO_30, emptyList);
		List<VisitsByPeriod> visits30to45 = visitsByDurationLastWeek.getOrDefault(MinutesRange.RANGE_30_TO_45, emptyList);
		List<VisitsByPeriod> visits45to60 = visitsByDurationLastWeek.getOrDefault(MinutesRange.RANGE_45_TO_60, emptyList);
		List<VisitsByPeriod> visits60toInf = visitsByDurationLastWeek.getOrDefault(MinutesRange.RANGE_60_TO_INF, emptyList);
		JsValue visits0to15Json = JsonHelper.toJson(takeLastWeek(visits0to15));
		JsValue visits15to30Json = JsonHelper.toJson(takeLastWeek(visits15to30));
		JsValue visits30to45Json = JsonHelper.toJson(takeLastWeek(visits30to45));
		JsValue visits45to60Json = JsonHelper.toJson(takeLastWeek(visits45to60));
		JsValue visits60toInfJson = JsonHelper.toJson(takeLastWeek(visits60toInf));

		return ok(views.html.admin.analytics.render(currentProfile,
				visitsByMonthJson,
				visitsByPeriodMaleJson, visitsByPeriodFemaleJson,
				visits0_8Json, visits8_11Json, visits11_13Json, visits13_16Json, visits16_20Json, visits20_24Json,
				visits0to15Json, visits15to30Json, visits30to45Json, visits45to60Json, visits60toInfJson,
				jsValue15, jsValue16, jsValue17, jsValue18));

	}

	@SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
	public Result connections() throws NoProfileFoundException {
		PortalNetworkConfiguration portalNetworkConfiguration = connectionsService.networkConfiguration(portalId());
		Form<PortalNetworkConfiguration> form = portalNetworkConfiguration == null
				? formFactory.form(PortalNetworkConfiguration.class)
				: formFactory.form(PortalNetworkConfiguration.class).fill(portalNetworkConfiguration);
		CommonProfile currentProfile = getCurrentProfile();
		ArrayList<ConnectedUser> connectedUsers = connectionsService.connectedUsers();
		return ok(views.html.admin.connections.render(form, connectedUsers, currentProfile));
	}

	@SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
	public Result surveys() throws NoProfileFoundException {
		CommonProfile currentProfile = getCurrentProfile();
		Form<Survey> form = formFactory.form(Survey.class);
		return ok(views.html.admin.surveys.render(currentProfile, form, true, true, 0, 0));
	}

	@SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
	public Result survey(Long surveyId) throws NoProfileFoundException {
		CommonProfile currentProfile = getCurrentProfile();
		Survey survey = new SurveyDAO().get(surveyId);
		Form<Survey> form = formFactory.form(Survey.class).fill(survey);
		return ok(views.html.admin.surveys.render(currentProfile, form, true, false, 0, 0));
	}

	@SubjectPresent(handlerKey = "FormClient", forceBeforeAuthCheck = true)
	public Result allSurveys() throws NoProfileFoundException {
		CommonProfile currentProfile = getCurrentProfile();
		GetAllSurveysResponse getAllSurveysResponse = surveysService.getAllSurveys(new GetAllSurveysRequest(portalId()));
		List<SurveySummary> summaries = getAllSurveysResponse.surveys().stream()
				.map(this::toSummary)
				.sorted(Comparator.comparing(SurveySummary::creation).reversed())
				.collect(Collectors.toList());
		return ok(views.html.admin.all_surveys.render(currentProfile, summaries));
	}

	public Result portalSettings() {
		return notFound();
	}

	private SurveySummary toSummary(Survey survey) {
		return new SurveySummary(survey.getId(), survey.getTitle(), survey.getWhenCreated());
	}

	private List<VisitsByPeriod> takeLastWeek(List<VisitsByPeriod> list) {
		int size = list.size();
		return list.subList(size - Math.min(size, 7), size);
	}

}
