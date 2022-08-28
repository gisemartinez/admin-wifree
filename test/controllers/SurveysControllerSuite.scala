package controllers

import controllers.admin.SurveyFormHelper
import daos.SurveyDAO
import io.ebean.Expr
import models._
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import play.api.data.Form
import play.api.data.Forms.{boolean, list, mapping, number, text}
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.test.Helpers
import play.test.Helpers.{GET, POST, contentAsString}
import utils.{SuiteHelper, WiFreeSuite}

import java.util.Calendar
import scala.collection.JavaConverters._;

class SurveysControllerSuite extends WiFreeSuite with SuiteHelper {
  import SurveysControllerSuite._
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

  @Test def saveSurvey(): Unit = {
    val (admin, portal) = adminWithPortal()
    val surveyId = util.Random.nextInt()
    val surveyTitle = "This is a survey with 1 field to answer"

    val surveyToMap = Json.obj(
      ("id", s"$surveyId"),
      ("portalId", s"${portal.getId}"),
      ("title", surveyTitle),
      ("enabled", true),
      (
        "fields",
        Json.arr(
          List(
            Json.obj(
              ("id", 1),
              ("type", FieldConfig.FieldConfigTypes.Textbox),
              (
                "config",
                Json.obj(
                  ("key", "key1"),
                  ("label", "label 1"),
                  ("order", 1),
                  ("required", false),
                  ("value", ""),
                  ("maximum", 0)
                )
              )
            )
          )
        )
      )
    )

    val saveRequestBuilder =
      Helpers.fakeRequest
        .method(POST)
        .uri(s"/api/survey")
        .bodyJson(surveyToMap)
    val saveResult = execAuthCallAs(admin, saveRequestBuilder)

    assertEquals(OK, saveResult.status)
    val saveResultStr = contentAsString(saveResult)
    assertTrue(saveResultStr.contains(surveyTitle))
  }

  @Test def createSurvey(): Unit = {
    val (admin, _) = adminWithPortal()
    val surveyTitle = "Created Survey at " + Calendar.getInstance().getTime()

    val surveyForm =
      SurveyForm(
        surveyTitle,
        true,
        List(
          FieldForm(
            FieldConfigForm(
              key = "key",
              label = "label",
              order = 1, // FieldConfig
              required = false,
              value = "some", // Textfield
              maximum = 0, // Rating
              options = List.empty[OptionForm]
            ),
            FieldConfig.FieldConfigTypes.Rating
          )
        )
      )
    val surveyToMap = form.fill(surveyForm).data

    val saveRequestBuilder =
      Helpers.fakeRequest
        .method(POST)
        .uri(s"/manage/surveys")
        .bodyForm(surveyToMap.asJava)
    val saveResult = execAuthCallAs(admin, saveRequestBuilder)

    assertEquals(SEE_OTHER, saveResult.status)
    val surveyDAO = new SurveyDAO

    val afterCreation =
      scala.Option(surveyDAO.find(Expr.eq("title", surveyTitle)))

    assertTrue(afterCreation.nonEmpty)
  }

  @Test def createSurveyFail(): Unit = {
    val (admin, _) = adminWithPortal()
    val surveyTitle = "Created Survey at " + Calendar.getInstance().getTime()

    val surveyForm =
      SurveyForm(
        surveyTitle,
        true,
        List.empty[FieldForm]
      )
    val surveyToMap = SurveysControllerSuite.form.fill(surveyForm).data

    val saveRequestBuilder =
      Helpers.fakeRequest
        .method(POST)
        .uri(s"/manage/surveys")
        .bodyForm(surveyToMap.asJava)
    val saveResult = execAuthCallAs(admin, saveRequestBuilder)

    assertEquals(BAD_REQUEST, saveResult.status)
    val surveyDAO = new SurveyDAO

    val afterCreation =
      scala.Option(surveyDAO.find(Expr.eq("title", surveyTitle)))

    assertTrue(afterCreation.isEmpty)
  }

  @Test def surveysTest(): Unit = {
    val (admin, _) = adminWithPortal()

    val requestBuilder =
      Helpers.fakeRequest
        .method(GET)
        .uri("/manage/surveys")

    val result = execAuthCallAs(admin, requestBuilder)
    assertEquals(OK, result.status)
  }

  @Test def surveyResults(): Unit = {
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
    val randomIdentifier = scala.util.Random.nextInt()

    val json: JsValue = Json.parse(s"""
{
	"user": "$randomIdentifier",
	"answers": [
		{
			"field": "1",
			"answer": "14"
		},
		{
			"field": "2",
			"answer": "OptionA"
		}
	]
}
  """)

    val session = sessionFor(admin)
    val answerSurveyRequest =
      Helpers.fakeRequest
        .method(POST)
        .session(session)
        .uri(s"/api/survey/answers")
        .bodyJson(json)

    val answerSurveyResult = Helpers.route(WiFreeSuite.app, answerSurveyRequest)

    val surveyResultsRequest =
      Helpers.fakeRequest
        .method(GET)
        .session(session)
        .uri(s"/data/surveys/${survey.getId}/results")

    val answeredSurveyRequest =
      Helpers.fakeRequest
        .method(GET)
        .session(session)
        .uri(s"/data/surveys/${survey.getId}/answers")

    val allAnswersSurveyRequest =
      Helpers.fakeRequest
        .method(GET)
        .session(session)
        .uri(s"/data/surveys/${survey.getId}/all-answers")
    val surveyResultsResult =
      Helpers.route(WiFreeSuite.app, surveyResultsRequest)

    val answeredSurveyResult =
      Helpers.route(WiFreeSuite.app, answeredSurveyRequest)

    val allAnswersSurveyResult =
      Helpers.route(WiFreeSuite.app, allAnswersSurveyRequest)

    val answerSurveyResultStr = contentAsString(answerSurveyResult)
    val adminResultsResultStr = contentAsString(surveyResultsResult)
    val answeredSurveyResultStr = contentAsString(answeredSurveyResult)
    val allAnswersSurveyResultStr = contentAsString(allAnswersSurveyResult)

    assertEquals(OK, answerSurveyResult.status)
    assertEquals(OK, surveyResultsResult.status)
    assertEquals(OK, answeredSurveyResult.status)
    assertEquals(OK, allAnswersSurveyResult.status)
    assertTrue(
      answerSurveyResultStr.contains(
        s"Survey answers succeeded: true, survey: 1, user: $randomIdentifier"
      )
    )
    assertTrue(
      adminResultsResultStr.contains(
        "OptionA"
      )
    )

    assertTrue(
      answeredSurveyResultStr.contains(
        "OptionA"
      )
    )

    assertTrue(
      allAnswersSurveyResultStr.contains(
        "OptionA"
      )
    )
  }
}

object SurveysControllerSuite {
  val form: Form[SurveyForm] = Form(
    mapping(
      "title" -> text,
      "enabled" -> boolean,
      "fields" -> list(fieldMapping)
    )(SurveyForm.apply)(SurveyForm.unapply)
  )

  private def fieldMapping = {
    mapping(
      "config" -> fieldConfigMapping,
      "type" -> text
    )(FieldForm.apply)(FieldForm.unapply)
  }

  private def fieldConfigMapping = {
    mapping(
      "key" -> text,
      "label" -> text,
      "order" -> number,
      "required" -> boolean,
      "value" -> text,
      "maximum" -> number,
      "options" -> list(optionMapping)
    )(FieldConfigForm.apply)(FieldConfigForm.unapply)
  }

  private def optionMapping = mapping(
    "key" -> text
  )(OptionForm.apply)(OptionForm.unapply)

  final case class SurveyForm(
      title: String,
      enabled: Boolean,
      fields: List[FieldForm]
  )

  final case class FieldForm(
      config: FieldConfigForm,
      fieldType: String
  )

  final case class FieldConfigForm(
      key: String,
      label: String,
      order: Int, // FieldConfig
      required: Boolean,
      value: String, // Textfield
      maximum: Int, // Rating
      options: List[OptionForm]
  ) // Radio

  final case class OptionForm(key: String)
}
