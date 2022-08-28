package operations.responses

import models.Survey
import operations.core.ResponseType.CREATE_SURVEY_RESPONSE
import operations.core.{ResponseType, WiFreeResponse}

case class CreateSurveyResponse(
    createdSurvey: Survey,
    success: Boolean,
    errors: List[String]
) extends WiFreeResponse {

  override def responseType: ResponseType = CREATE_SURVEY_RESPONSE

  override def isOk(): Boolean = success

}
