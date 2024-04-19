package com.challenge.app.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model. StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.challenge.app.services._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import spray.json._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.challenge.app.entities.{Myrequest, salesOverAllRow, salesOverAllRowReturn, salesOverTimePerDay, salesOverTimePerMonth}
import com.challenge.app.services.{CassandraService, loginService, salesOverALll, signinSerivice}

import scala.concurrent.duration._

trait MyJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val request = jsonFormat2(Myrequest)
  implicit val salesRow = jsonFormat11(salesOverAllRowReturn)
  implicit val salesperday = jsonFormat4(salesOverTimePerDay)
  implicit val salespermonth = jsonFormat5(salesOverTimePerMonth)
}

object MyRoutes extends MyJsonProtocol {
  val ServerRoutes: Route = cors() {

    def authenticate(route: => Route): Route = optionalHeaderValueByName("Authorization") {
      case Some(token) =>
        if (loginService.isTokenValid(token)) {
          if (loginService.isTokenExpired(token)) {
            complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token expired."))
          } else {
            route // Execute the route inside the authenticated block
          }
        } else {
          complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token is invalid, or has been tampered with."))
        }
      case _ => complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "No token provided!"))
    }
    val GetOverAllSales: Route =
      (path("secureEndpoint") & get) {
        optionalHeaderValueByName("Authorization") {
          case Some(token) =>
            if (loginService.isTokenValid(token)) {
              if (loginService.isTokenExpired(token)) {
                complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token expired."))
              } else {
                parameters(
                  'str,
                  'chnl,
                  'regn,
                  'sku,
                  'item,
                  'item_type,
                  'subclass,
                  'Class,
                  'dept,
                  'dvsn,
                  'limit,
                  'lastsku,
                  'laststr,
                ) { (str, chnl, regn, sku, item, item_type, subclass, Class, dept, dvn,limit,lastsku,laststr) =>
                  complete(salesOverALll.selectRows(salesOverAllRow(str,chnl,regn,sku,item,item_type,subclass,Class,dept,dvn),limit.toInt,lastsku,laststr))
                }
              }
            } else {
              complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "Token is invalid, or has been tampered with."))
            }
          case _ => complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "No token provided!"))
        }
      }

    val GetOverAllSalesNumber: Route =
      (path("secureEndpointN") & get) {
        authenticate {
          parameters(
            'str,
            'chnl,
            'regn,
            'sku,
            'item,
            'item_type,
            'subclass,
            'Class,
            'dept,
            'dvsn,
            'limit,
            'lastsku,
            'laststr,
          ) { (str, chnl, regn, sku, item, item_type, subclass, Class, dept, dvn,limit,lastsku,laststr) =>
            complete(salesOverALll.getNbrOFRows(salesOverAllRow(str,chnl,regn,sku,item,item_type,subclass,Class,dept,dvn),limit.toInt,lastsku,laststr).toString)
          }
        }
      }

    val GetSumSalesInRangePerDay: Route =
      (path("secureEndpoint2") & get) {
        authenticate {
          parameters(
            'start,
            'end,
            'sku,
            'str
          ) { (start,end, sku,str) =>
            complete( status = StatusCodes.OK,salesOverTime.getSumFromRange2(start.toInt,end.toInt, sku,str).toString)
          }
        }
      }

    val GetSalesInRangePerDay: Route =
      (path("secureEndpoint3") & get) {
        authenticate {
          parameters(
            'start,
            'end,
            'sku,
            'str,
            'lastday,
            'limit
          ) { (start,end, sku,str,lastday,limit) =>
            complete(salesOverTime.selectRowsPerDay(start.toInt,end.toInt, sku,str,lastday.toInt,limit.toInt))
          }
        }
      }

    val GetSalesInRangePerMonth: Route =
      (path("secureEndpoint4") & get) {
        authenticate {
          parameters(
            'start,
            'end,
            'sku,
            'str,
            'lastmonth,
            'limit
          ) { (start,end, sku,str,lastmonth,limit) =>
            complete(salesOverTime.selectRowsPermonth(start.toInt,end.toInt, sku,str,lastmonth.toInt,limit.toInt))
          }
        }
      }

    val loginRoute: Route =path("login") {
      post {
        entity(as[Myrequest]) { Request =>
          val email = Request.email
          val password = Request.password
          if (loginService.checkPassword(email, password)) {
            complete(loginService.createToken(email, 1))
          } else
            complete(StatusCodes.Unauthorized)
        }
      }
    }

    val signinRoute: Route =path("signin") {
      post {
        entity(as[Myrequest]) { Request =>
          val email = Request.email
          val password = Request.password
          signinSerivice.signin(email, password)
        }
      }
    }

    loginRoute ~ signinRoute ~ GetOverAllSales ~ GetSumSalesInRangePerDay ~ GetSalesInRangePerDay ~ GetSalesInRangePerMonth ~ GetOverAllSalesNumber
  }
}
object RestAPI extends App with MyJsonProtocol{
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(2.seconds)
  CassandraService.initialize()
  sparkService.loading("salestomonthlevel")
  sparkService.loading("merged_salesoverall")
  sparkService.loading("sales")

  val TestingDuration = 2 * 60 * 60 * 1000
  println("Application is running go to http://localhost:3000/")
  Http().newServerAt("0.0.0.0", 8080).bind(MyRoutes.ServerRoutes)
  Thread.sleep(TestingDuration)
}
