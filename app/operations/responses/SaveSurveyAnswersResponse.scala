package operations.responses

import operations.core.{ResponseType, WiFreeResponse}

case class SaveSurveyAnswersResponse(success: Boolean, surveyId: Long, userId: Long) extends WiFreeResponse {

  override def responseType: ResponseType = ResponseType.RUN_ANALYTICS_QUERY_FILTER_RESPONSE

  override val errors: List[String] = Nil

  override def isOk(): Boolean = ???

}
