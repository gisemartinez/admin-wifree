package operations.responses

import operations.core.{ResponseType, WiFreeResponse}

case class RunAnalyticsQueryFilterResponse(
    success: Boolean,
    errors: List[String]
) extends WiFreeResponse {
  // TODO agregar atributo(s) con los resultados

  override def responseType: ResponseType =
    ResponseType.RUN_ANALYTICS_QUERY_FILTER_RESPONSE

  override def isOk(): Boolean = true

}
