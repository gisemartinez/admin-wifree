package controllers.admin

import models.Survey
import operations.responses.GetAllSurveysResponse
import views.dto.SurveySummary

import scala.collection.JavaConverters._

object SurveyFormHelper {
  def buildSummaries(
      getAllSurveysResponse: GetAllSurveysResponse
  ): java.util.List[SurveySummary] = {
    getAllSurveysResponse.surveys.asScala.toList
      .map(toSummary)
      .sortBy(_.creation.getTime.toInt)(Ordering.Int.reverse)
      .asJava
  }

  def toSummary(survey: Survey) =
    SurveySummary(survey.getId, survey.getTitle, survey.getWhenCreated)
}
