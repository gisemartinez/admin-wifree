package controllers

import org.junit.Assert.assertEquals
import org.junit.Test
import play.api.libs.json.Json
import play.mvc.Http.Status.OK
import play.test.Helpers
import play.test.Helpers.POST
import utils.{SuiteHelper, WiFreeSuite}

class SocialUserControllerSuite extends WiFreeSuite with SuiteHelper {
  @Test def saveSocialUser(): Unit = {
    val (admin, portal) = adminWithPortal()

    val json =
      s"""{
       |    "portalId": ${portal.getId},
       |	"names": [
       |		{
       |			"displayName": "G M"
       |		}
       |	],
       |	"genders": [
       |		{
       |			"formattedValue": "Female"
       |		}
       |	],
       |	"birthdays": [
       |		{
       |			"date": {
       |				"year": 1990,
       |				"month":4,
       |				"day":26
       |			}
       |		}
       |	],
       |	"emailAddresses": [
       |		{
       |			"value": "gm@example.co"
       |		}
       |	]
       |}""".stripMargin

    val socialUserRequest =
      Helpers.fakeRequest
        .method(POST)
        .uri(s"/api/socialuser")
        .bodyJson(Json.parse(json))

    val socialUserResult = execAuthCallAs(admin, socialUserRequest)

    assertEquals(OK, socialUserResult.status)
  }

  @Test def saveUserConnected(): Unit = {
    val (admin, portal) = adminWithPortal()

    val json =
      s"""{
         |   "portalId": ${portal.getId},
         |	  "name": "username",
         |    "email": "jf@example.co",
         |    "gender": "Female",
         |	  "socialNetwork": "facebook",
         |	"age": 23
         |}""".stripMargin

    val socialUserRequest =
      Helpers.fakeRequest
        .method(POST)
        .uri(s"/api/user-connected")
        .bodyJson(Json.parse(json))

    val socialUserResult = execAuthCallAs(admin, socialUserRequest)

    assertEquals(OK, socialUserResult.status)
  }
}
