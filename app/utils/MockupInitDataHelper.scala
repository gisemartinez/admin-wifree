package utils

import java.time.{Duration, Instant}
import java.util
import java.util.concurrent.ThreadLocalRandom

import daos.{NetworkUserConnectionLogDAO, NetworkUserDAO, PortalDAO}
import models._
import models.types.Gender

import scala.util.Random

object MockupInitDataHelper {

  private val randomizer = new Random()
  private val end: Instant = DateHelper.now()
  private val start: Instant = DateHelper.oneYearAgo(end)
  private val recentStart: Instant = DateHelper.oneDayAgo(end)
  private val veryRecentStart: Instant = DateHelper.fifteenMinutesAgo(end)

  def run(pid: Long): Unit = {
    val portalDao = new PortalDAO()
    val networkUserDao = new NetworkUserDAO()
    val logDao = new NetworkUserConnectionLogDAO()

    val portal = portalDao.get(pid)

    (1 to 400).foreach { uid =>
      val userName = s"Usuario Prueba $pid $uid"
      val userEmail = s"usuarioprueba_${pid}_${uid}@mail.com"
      val userOnline = random(Seq(true, false))
      val userGender = random(Seq(Gender.Male, Gender.Female, Gender.Other))
      val userAge = random(13 to 90)

      val macAddresses = new util.HashSet[NetworkUserMACAddress]()
      val socialNetworkAccounts = new util.HashSet[NetworkUserSocialNetworkAccount]()
      val networkUser = new NetworkUser(portal, userName, userEmail, "", null, userOnline, "password", userGender, null, macAddresses, socialNetworkAccounts, userAge)
      networkUserDao.save(networkUser)

      (1 to random(1 to 30)).foreach { _ =>
        val plusEndMinutes = random(2 to 65)
        val startConnection = randomDate()
        val endConnection = startConnection.plus(Duration.ofMinutes(plusEndMinutes))
        val mac = randomMAC()

        val log = new NetworkUserConnectionLog(portal, networkUser, startConnection, endConnection, mac)
        logDao.save(log)
      }
    }

  }

  private def random[A](seq: Seq[A]): A = seq(randomizer.nextInt(seq.length))

  private def randomDate(): Instant = {
    val selectedStart = ThreadLocalRandom.current().nextInt(1, 26) match {
      case 1 => veryRecentStart
//      case 1 => recentStart
      case _ => start
    }

    val startSeconds = selectedStart.getEpochSecond
    val endSeconds = end.getEpochSecond
    val rnd = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds)
    Instant.ofEpochSecond(rnd)
  }

  private def randomMAC(): String = {
    var mac: String = ""
    (1 to 6).foreach { _ =>
      val n: java.lang.Integer = randomizer.nextInt(255)
      mac = mac + String.format("%02x", n)
    }
    mac.toUpperCase()
  }

}
