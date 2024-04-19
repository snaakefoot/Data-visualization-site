package com.challenge.app.services

import org.mindrot.jbcrypt.BCrypt
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtSprayJson}
import scala.util.{Failure, Success}
import java.util.concurrent.TimeUnit

trait loginServiceI {
  def checkPassword(email : String,password:String):Boolean
  def createToken(username: String, expirationPeriodInDays: Int): String
  def isTokenExpired(token: String): Boolean
  def isTokenValid(token: String): Boolean
}

object loginService extends loginServiceI {
  val algorithm = JwtAlgorithm.HS256
  val secretKey = "ChallengeAtCognira"
  override def  checkPassword(email : String,password:String):Boolean ={
    if(CassandraService.checkAccount(email))
       BCrypt.checkpw(password, CassandraService.getPassword(email))
    else
      false
  }
  override def createToken(username: String, expirationPeriodInDays: Int): String = {
    val claims = JwtClaim(
      expiration = Some(System.currentTimeMillis() / 1000 + TimeUnit.DAYS.toSeconds(expirationPeriodInDays)),
      issuedAt = Some(System.currentTimeMillis() / 1000),
    )

    JwtSprayJson.encode(claims, secretKey, algorithm) // JWT string
  }
  override def isTokenExpired(token: String): Boolean = JwtSprayJson.decode(token, secretKey, Seq(algorithm)) match {
    case Success(claims) => claims.expiration.getOrElse(0L) < System.currentTimeMillis() / 1000
    case Failure(_) => true
  }
  override def isTokenValid(token: String): Boolean = JwtSprayJson.isValid(token, secretKey, Seq(algorithm))

}