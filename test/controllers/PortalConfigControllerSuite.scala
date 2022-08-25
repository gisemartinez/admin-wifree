package controllers

import org.junit.Assert.assertEquals
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

    val expectedResult =
      s"""{"landingChoices":{"title":"${portal.getName}"},"client_id":"${portal.getId}","template_id":"template-2"}"""
    assertEquals(requestResultStr, expectedResult)
  }

  @Test def clientAuth(): Unit = {
    val (_, portal) = adminWithPortal()
    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri(s"/api/client-auth/${portal.getId}")

    val requestResult = Helpers.route(WiFreeSuite.app, requestBuilder)

    val requestResultStr = contentAsString(requestResult)

    assertEquals(OK, requestResult.status)

    val expectedResult =
      s"""{"authData":{"loginTypes":["survey","socialLogin"],"loginTypeOptions":[{"socialMediaKeys":[]}],"client_id":"${portal.getId}"},"clientData":{"clientId":"${portal.getId}","name":"${portal.getName}","description":"${portal.getDescription}"},"carouselData":{"images":[]}}"""
    assertEquals(expectedResult, requestResultStr)
  }
}
