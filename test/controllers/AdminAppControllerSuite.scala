package controllers

import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import play.mvc.Http.Status.OK
import play.test.Helpers
import play.test.Helpers.{GET, contentAsString}
import utils.{SuiteHelper, WiFreeSuite}

class AdminAppControllerSuite extends WiFreeSuite with SuiteHelper {
  @Test def dashboardTest(): Unit = {
    val (admin, _) = adminWithPortal()

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/dashboard")

    val result = execAuthCallAs(admin, requestBuilder)
    val text = contentAsString(result)
    assertEquals(OK, result.status)
    assertTrue(text.contains("Usuarios Conectados"))
  }

  @Test def analyticsTest(): Unit = {
    val (admin, _) = adminWithPortal()

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/data/visitors")

    val result = execAuthCallAs(admin, requestBuilder)
    assertEquals(OK, result.status)
  }

  @Test def connectionsTest(): Unit = {
    val (admin, _) = adminWithPortal()

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/connections")

    val result = execAuthCallAs(admin, requestBuilder)
    assertEquals(OK, result.status)
  }

  @Test def loginSettingsTest(): Unit = {
    val (admin, _) = adminWithPortal()

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/manage/social-login")

    val result = execAuthCallAs(admin, requestBuilder)
    assertEquals(OK, result.status)
  }

  @Test def portalSettingsTest(): Unit = {
    val (admin, _) = adminWithPortal()

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/manage/portal-settings")

    val result = execAuthCallAs(admin, requestBuilder)
    assertEquals(OK, result.status)
  }

  @Test def allCollectedSocialDataTest(): Unit = {
    val (admin, _) = adminWithPortal()

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/data/social-login/results")

    val result = execAuthCallAs(admin, requestBuilder)
    assertEquals(OK, result.status)
  }
}
