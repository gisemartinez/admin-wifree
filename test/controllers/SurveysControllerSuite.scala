package controllers

import controllers.api.dto.FieldAnswerDTO
import daos.{AdminDAO, PortalDAO, SurveyDAO}
import io.ebean.Expr
import models._
import models.types.AccountType
import operations.requests.SaveSurveyAnswersRequest
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import play.mvc.Http.Status.{OK, SEE_OTHER}
import play.test.Helpers
import play.test.Helpers.{GET, POST, contentAsString}
import services.SurveysService
import utils.{SuiteHelper, WiFreeSuite}

import scala.collection.JavaConverters._;

class SurveysControllerSuite extends WiFreeSuite with SuiteHelper {
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

  @Test def listAFewSurveys(): Unit = {
    val (admin, portal) = adminWithPortal()

    val surveyDAO = new SurveyDAO

    val surveyA =
      new Survey(
        1,
        portal,
        "This is a survey without fields to answer",
        List.empty[Field].asJava,
        true
      )

    val surveyB =
      new Survey(
        2,
        portal,
        "This is a survey with fields to answer",
        List(
          new Field(1, FieldConfig.FieldConfigTypes.Radio, new FieldConfig())
        ).asJava,
        true
      )
    surveyDAO.save(surveyA)
    surveyDAO.save(surveyB)

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/data/surveys")

    val result = execAuthCallAs(admin, requestBuilder)
    val text = contentAsString(result)
    assertEquals(OK, result.status)
    assertTrue(text.contains(surveyA.getTitle))
    assertTrue(text.contains(surveyB.getTitle))
  }

  @Test def listJustOneSurvey(): Unit = {
    val (admin, portal) = adminWithPortal()

    val surveyDAO = new SurveyDAO

    val survey =
      new Survey(
        1,
        portal,
        "This is a survey with 1 field to answer",
        List(
          new Field(1, FieldConfig.FieldConfigTypes.Radio, new FieldConfig())
        ).asJava,
        true
      )

    surveyDAO.save(survey)

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri(s"/data/surveys/${survey.getId}")

    val result = execAuthCallAs(admin, requestBuilder)
    val text = contentAsString(result)
    assertEquals(OK, result.status)
    assertTrue(text.contains(survey.getTitle))
  }

  @Test def deleteSurvey(): Unit = {
    val (admin, portal) = adminWithPortal()

    val surveyDAO = new SurveyDAO

    val survey =
      new Survey(
        1,
        portal,
        "This is a survey with 1 field to answer",
        List(
          new Field(1, FieldConfig.FieldConfigTypes.Textbox, new FieldConfig())
        ).asJava,
        true
      )

    surveyDAO.save(survey)
    val beforeDeletion =
      scala.Option(surveyDAO.find(Expr.eq("id", survey.getId)))

    val surveyRequest =
      Helpers.fakeRequest
        .method(POST)
        .uri(s"/manage/surveys/delete/${survey.getId}")
    val surveyResult = execAuthCallAs(admin, surveyRequest)

    val afterDeletion =
      scala.Option(surveyDAO.find(Expr.eq("id", survey.getId)))

    assertEquals(SEE_OTHER, surveyResult.status)
    assertTrue(beforeDeletion.nonEmpty)
    assertTrue(afterDeletion.isEmpty)
  }

  @Test def listSurveyAnswers(): Unit = {
    val (admin, portal) = adminWithPortal()
    val surveyDAO = new SurveyDAO
    val survey =
      new Survey(
        1,
        portal,
        "This is a survey with 1 field to answer",
        List(
          new Field(
            1,
            FieldConfig.FieldConfigTypes.Textbox,
            new FieldConfig("key1", "label 1", 1, false, null, null, null)
          ),
          new Field(
            2,
            FieldConfig.FieldConfigTypes.Checkbox,
            new FieldConfig(
              "key2",
              "label 2",
              2,
              false,
              null,
              null,
              List(
                new models.Option(1, "OptionA"),
                new models.Option(2, "OptionB")
              ).asJava
            )
          )
        ).asJava,
        true
      )
    surveyDAO.save(survey)

    val surveysService: SurveysService =
      WiFreeSuite.app.injector().instanceOf(classOf[SurveysService])

    surveysService.saveSurveyAnswers(
      SaveSurveyAnswersRequest(
        List(
          new FieldAnswerDTO(1, "Some Text"),
          new FieldAnswerDTO(2, "OptionA,OptionB ")
        ).asJava,
        admin.getId
      )
    )

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri(s"/data/surveys/${survey.getId}/results")
    val result = execAuthCallAs(admin, requestBuilder)
    val text = contentAsString(result)
    assertEquals(OK, result.status)
    assertTrue(
      text.contains("Some Text")
    )
  }
}
