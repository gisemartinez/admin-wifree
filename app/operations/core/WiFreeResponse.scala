package operations.core

trait WiFreeResponse extends Operation {

  val success: Boolean
  val errors: List[String] // TODO TBD

  override def operationType: OperationType = OperationType.RESPONSE

  def responseType: ResponseType

  def isOk: Boolean

}
