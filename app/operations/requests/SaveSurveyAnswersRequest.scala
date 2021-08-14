package operations.requests

import controllers.api.dto.FieldAnswerDTO
import operations.core.{RequestType, WiFreeRequest}

import java.util.{List => JList}

case class SaveSurveyAnswersRequest(fieldsAnswers: JList[FieldAnswerDTO], userId: Long) extends WiFreeRequest {

  override def requestType: RequestType = RequestType.SAVE_SURVEY_ANSWERS_REQUEST

}
