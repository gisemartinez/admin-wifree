package controllers

import daos.SurveyDAO
import models.{Field, FieldConfig, Survey}
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status.OK
import play.test.Helpers
import play.test.Helpers.{POST, contentAsString}
import utils.{SuiteHelper, WiFreeSuite}

import scala.collection.JavaConverters._;

class SurveyAnswersControllerSuite extends WiFreeSuite with SuiteHelper {
  @Test def saveSurveyAnswers(): Unit = {
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

    val answerSurveyResultStr = contentAsString(answerSurveyResult)

    assertEquals(OK, answerSurveyResult.status)

    assertTrue(
      answerSurveyResultStr.contains(
        s"Survey answers succeeded: true, survey: 1, user: $randomIdentifier"
      )
    )
  }
}
