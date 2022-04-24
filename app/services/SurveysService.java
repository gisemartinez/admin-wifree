package services;

import daos.SurveyDAO;
import models.Survey;
import operations.requests.CreateSurveyRequest;
import operations.requests.GetAllSurveysRequest;
import operations.requests.SaveSurveyAnswersRequest;
import operations.responses.CreateSurveyResponse;
import operations.responses.GetAllSurveysResponse;
import operations.responses.SaveSurveyAnswersResponse;
import services.core.functions.CreateSurveyFunction;
import services.core.functions.GetAllSurveysFunction;
import services.core.functions.SaveSurveyAnswersFunction;

import javax.inject.Inject;

public class SurveysService extends WiFreeService {

    @Inject
    CreateSurveyFunction createSurvey;

    @Inject
    GetAllSurveysFunction getAllSurveys;

    @Inject
    SaveSurveyAnswersFunction saveSurveyAnswers;

    @Inject
    private SurveyDAO surveyDAO;

    public CreateSurveyResponse createSurvey(CreateSurveyRequest createSurveyRequest) {
        return createSurvey.apply(createSurveyRequest);
    }

    public GetAllSurveysResponse getAllSurveys(GetAllSurveysRequest getAllSurveysRequest) {
        return getAllSurveys.apply(getAllSurveysRequest);
    }

    public SaveSurveyAnswersResponse saveSurveyAnswers(SaveSurveyAnswersRequest saveSurveyAnswersRequest) {
        return saveSurveyAnswers.apply(saveSurveyAnswersRequest);
    }

    public void deleteSurvey(Survey survey) {
        surveyDAO.delete(survey);
    }
}
