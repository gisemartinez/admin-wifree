package controllers

import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import play.mvc.Http.Status.OK
import play.test.Helpers
import play.test.Helpers.{GET, contentAsString, route}
import utils.WiFreeSuite

import scala.collection.JavaConverters._

class LoginControllerSuite extends WiFreeSuite {
  @Test def loginWithoutErrors(): Unit = {
    val loginRequest =
      Helpers.fakeRequest
        .method(GET)
        .uri("/login")

    val authResult = route(WiFreeSuite.app, loginRequest)
    assertEquals(OK, authResult.status)
    val authResultStr = contentAsString(authResult)
    assertTrue(!authResultStr.contains("CredentialsException"))
  }

  @Test def loginWithErrors(): Unit = {
    val loginRequest =
      Helpers.fakeRequest
        .method(GET)
        .uri("/login?username=bret%40famly.co&error=CredentialsException")

    val authResult = route(WiFreeSuite.app, loginRequest)
    val authResultStr = contentAsString(authResult)
    assertEquals(OK, authResult.status)
    assertTrue(authResultStr.contains("CredentialsException"))
  }
}
