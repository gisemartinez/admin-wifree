package controllers

import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import play.mvc.Http.Status.OK
import play.test.Helpers
import play.test.Helpers.{GET, contentAsString}
import utils.{SuiteHelper, WiFreeSuite}

class PortalConfigControllerSuite extends WiFreeSuite with SuiteHelper {
  @Test def clientLanding(): Unit = {
    val (_, portal) = adminWithPortal()
    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri(s"/api/client-landing/${portal.getId}")

    val requestResult = Helpers.route(WiFreeSuite.app, requestBuilder)

    val requestResultStr = contentAsString(requestResult)

    assertEquals(OK, requestResult.status)

    assertTrue(
      requestResultStr.contains(
        s"""{"uniqueId":"a622f7c9-2696-4d15-9396-0c3273ea06a5",
          |"landingChoices":{"title":"${portal.getName}"},
          |"client_id":"${portal.getId}",
          |"template_id":"template-2"}""".stripMargin
      )
    )
  }

  def clientAuth(): Unit = {
    val (_, portal) = adminWithPortal()
    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri(s"/api/client-auth/${portal.getId}")

    val requestResult = Helpers.route(WiFreeSuite.app, requestBuilder)

    val requestResultStr = contentAsString(requestResult)

    assertEquals(OK, requestResult.status)

    assertTrue(
      requestResultStr.contains(
        s"Survey answers succeeded: true, survey: 1, user:"
      )
    )
  }
}
