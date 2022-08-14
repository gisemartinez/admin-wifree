package controllers

import org.junit.Assert.assertEquals
import org.junit.Test
import play.api.test.Helpers.GET
import play.mvc.Http.Status.OK
import play.test.Helpers
import utils.WiFreeSuite

class ConsoleControllerSuite extends WiFreeSuite {
  @Test def indexTest(): Unit = {

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/console")

    val authResult = Helpers.route(WiFreeSuite.app, requestBuilder)
    assertEquals(OK, authResult.status)
  }
}
