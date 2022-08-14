package utils

import daos.{AdminDAO, PortalDAO}
import models.types.AccountType
import models.{Admin, Portal}
import org.junit.Assert.assertEquals
import play.mvc.Http.RequestBuilder
import play.mvc.Http.Status.SEE_OTHER
import play.mvc.{Http, Result}
import play.test.Helpers
import play.test.Helpers.{POST, route}

import scala.collection.JavaConverters._

trait SuiteHelper {
  def adminWithPortal(): (Admin, Portal) = {
    val admin =
      new Admin(null, "Don Admin", "donadmin@mail.com", "donadmin", null, false)
    val adminDAO = new AdminDAO
    adminDAO.save(admin)

    // Portal
    val portal = new Portal(
      "Test Portal",
      "Portal de pruebas",
      AccountType.Basic,
      admin,
      null,
      null,
      null,
      null,
      null
    )

    val portalDAO = new PortalDAO
    portalDAO.save(portal)

    (admin, portal)
  }

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

  def sessionFor(admin: Admin): Http.Session = {
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

    authResult.session()
  }
}
