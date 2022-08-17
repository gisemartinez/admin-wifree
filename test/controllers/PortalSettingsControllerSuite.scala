package controllers

import akka.stream.javadsl.Source
import akka.util.ByteString
import org.junit.Assert.assertEquals
import org.junit.Test
import play.api.data.Form
import play.api.data.Forms.{boolean, list, mapping, optional, text}
import play.mvc.Http.MultipartFormData
import play.mvc.Http.Status.SEE_OTHER
import play.test.Helpers
import play.test.Helpers.{POST, contentAsString}
import utils.{SuiteHelper, WiFreeSuite}

import scala.collection.JavaConverters._

class PortalSettingsControllerSuite extends WiFreeSuite with SuiteHelper {
  @Test def savePortalOptions(): Unit = {
    val admin = createAdmin()
    val portalOptions =
      PortalSettingsHelper.PortalOptions(
        "portal name",
        "description",
        "homeUrl",
        List("SocialLogin"),
        PortalSettingsHelper
          .AppType(
            "TemplateOne",
            None,
            true,
            None
          ),
        PortalSettingsHelper.AppType(
          "Carousel",
          None,
          true,
          Some(PortalSettingsHelper.Config(images = List()))
        )
      )

    val form = PortalSettingsHelper.form.fill(portalOptions).data
    val formToMultipart: List[MultipartFormData.Part[Source[ByteString, _]]] =
      form.map { case (k, v) => multipartFrom(k, v) }.toList

    val session = sessionFor(admin)

    val part: MultipartFormData.Part[Source[ByteString, _]] = multipart(
      "image_1_0.jpeg"
    )
    val saveRequest =
      Helpers.fakeRequest
        .method(POST)
        .session(session)
        .uri(s"/manage/portal-settings")
        .bodyMultipart(
          (part :: formToMultipart).asJava,
          play.libs.Files.singletonTemporaryFileCreator(),
          WiFreeSuite.app.getWrappedApplication().materializer
        )

    val saveResult = Helpers.route(WiFreeSuite.app, saveRequest)

    val saveResultStr = contentAsString(saveResult)

    assertEquals(SEE_OTHER, saveResult.status)
  }
}

object PortalSettingsHelper {

  val form: Form[PortalOptions] = Form(
    mapping(
      "name" -> text,
      "description" -> text,
      "homeURL" -> text,
      "loginMethods" -> list(text),
      "template" -> appTypeMapping,
      "carousel" -> appTypeMapping
    )(PortalOptions.apply)(PortalOptions.unapply)
  )

  def appTypeMapping = mapping(
    "type" -> text,
    "name" -> optional(text),
    "enabled" -> boolean,
    "config" -> optional(configMapping)
  )(AppType.apply)(AppType.unapply)

  def configMapping = mapping(
    "images" -> list(text)
  )(Config.apply)(Config.unapply)

  final case class PortalOptions(
      name: String,
      description: String,
      homeUrl: String,
      loginMethods: List[String],
      template: AppType,
      carousel: AppType
  )
  final case class AppType(
      `type`: String,
      name: Option[String],
      enabled: Boolean,
      config: Option[Config]
  )
  final case class Config(images: List[String])
}
