package controllers.admin

import models.Survey
import operations.responses.GetAllSurveysResponse
import play.api.data.Form
import play.api.data.Forms.{boolean, list, mapping, number, text}
import views.dto.SurveySummary

import scala.collection.JavaConverters._

object SurveyFormHelper {

  val form: Form[SurveyForm] = Form(
    mapping(
      "title" -> text,
      "enabled" -> boolean,
      "fields" -> list(fieldConfigMapping)
    )(SurveyForm.apply)(SurveyForm.unapply)
  )

  private def fieldConfigMapping = {
    mapping(
      "fieldType" -> text,
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

  def buildSummaries(
      getAllSurveysResponse: GetAllSurveysResponse
  ): java.util.List[SurveySummary] = {
    getAllSurveysResponse.surveys.asScala.toList
      .map(toSummary)
      .sortBy(_.creation.getTime.toInt)(Ordering.Int.reverse)
      .asJava
  }

  def toSummary(survey: Survey) =
    new SurveySummary(survey.getId, survey.getTitle, survey.getWhenCreated)
}

case class SurveyForm(
    title: String,
    enabled: Boolean,
    fields: List[FieldConfigForm]
) {}

case class FieldConfigForm(
    fieldType: String, // Field
    key: String,
    label: String,
    order: Int, // FieldConfig
    required: Boolean,
    value: String, // Textfield
    maximum: Int, // Rating
    options: List[OptionForm]
) // Radio

case class OptionForm(key: String)
