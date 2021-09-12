package operations.responses

import models.types.Gender
import operations.core.{ResponseType, WiFreeResponse}

import java.util.{Map => JMap}
import java.lang.{Long => JLong}

case class GetDashboardDataResponse(usersConnectedLastWeek: Long,
                                    usersByAgeLastWeek: JMap[(Integer, Integer), JLong],
                                    newUsersLastWeek: Long,
                                    usersByGenderLastWeek: JMap[Gender, JLong],
                                    busiestDayLastWeek: String,
                                    busiestTimeLastWeek: String,
                                    usersLoyalty: Double,
                                    usersOnline: Long) extends WiFreeResponse {

  override val success: Boolean = true
  override val errors: List[String] = Nil

  override def responseType: ResponseType = ResponseType.GET_DASHBOARD_DATA_RESPONSE

  override def isOk(): Boolean = true

}
