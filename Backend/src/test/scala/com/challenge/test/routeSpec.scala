package com.challenge.test

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import com.challenge.app.routes.MyJsonProtocol
import com.challenge.app.entities.Myrequest
import com.challenge.app.services.loginService.isTokenValid

class routeSpec extends WordSpec with Matchers with ScalatestRouteTest with MyJsonProtocol {
  import com.challenge.app.routes.MyRoutes
    "Login Function" should {
      "succssfull login" in {
        val loginRequest=Myrequest("123@123","123")
        Post("/login",loginRequest)~>MyRoutes.ServerRoutes~> check {
          status shouldBe StatusCodes.OK
          isTokenValid(responseAs[String]) shouldBe true
        }
      }
      "failed login" in {
        val loginRequest=Myrequest("123@123","1234")
        Post("/login",loginRequest)~>MyRoutes.ServerRoutes~> check {
          status shouldBe StatusCodes.Unauthorized
        }
      }
    }
}