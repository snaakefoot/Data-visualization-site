package com.challenge.app.services

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.StandardRoute
import org.mindrot.jbcrypt.BCrypt
import akka.http.scaladsl.server.directives.RouteDirectives.complete
trait signinSeriviceI {
  def signin(email : String,password:String):StandardRoute
}

object signinSerivice extends signinSeriviceI {

  override def signin(email: String, password: String) = {
    if(!CassandraService.checkAccount(email)){
      CassandraService.addUser(email,BCrypt.hashpw(password, BCrypt.gensalt()))
      complete("done")
    }
    else
      complete(StatusCodes.Conflict)

  }
}