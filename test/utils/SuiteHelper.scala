package utils

import models.Admin
import org.junit.Assert.assertEquals
import play.mvc.Http.RequestBuilder
import play.mvc.Http.Status.SEE_OTHER
import play.mvc.Result
import play.test.Helpers
import play.test.Helpers.{POST, route}

import scala.collection.JavaConverters._

trait SuiteHelper {
  def execAuthCallAs(admin: Admin, requestBuilder: RequestBuilder): Result = {
    val loginUserParams =
      Map(
        "username" -> admin.getEmail,
        "password" -> "donadmin",
        "submit" -> "Submit"
      )

    val loginRequest =
      Helpers.fakeRequest
        .method(POST)
        .bodyForm(loginUserParams.asJava)
        .uri("/callback?client_name=FormClient")

    val authResult = route(WiFreeSuite.app, loginRequest)
    assertEquals(SEE_OTHER, authResult.status)

    val requestBuilderWithSession =
      requestBuilder
        .session(authResult.session())

    route(WiFreeSuite.app, requestBuilderWithSession)
  }
}
