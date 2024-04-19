package com.challenge.app.services

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import com.challenge.app.entities
import spray.json._
import com.challenge.app.entities.{salesOverAllRow, salesOverAllRowReturn}

trait salesOverALllI {
  def selectRows(row:salesOverAllRow,limit:Int,lastsku:String,laststr:String):List[salesOverAllRowReturn]
  def constructQuery(row:salesOverAllRow,limit:Int,lastsku:String,laststr:String):String
  def getNbrOFRows(row:salesOverAllRow,limit:Int,lastsku:String,laststr:String):Int
}


trait MyJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val salesRow = jsonFormat11(entities.salesOverAllRowReturn)
}


object salesOverALll extends MyJsonProtocol with salesOverALllI {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val country="US"
  val retailer="retailer"
  override def constructQuery(row:salesOverAllRow,limit:Int,lastsku:String,laststr:String)={
    val q = s"SELECT * FROM challenge.merged_salesoverall WHERE cmpy='$retailer' AND cntry='$country' "
    val interq = q + {
      if (row.str != "Any")
        s"AND str='${row.str}' "
      else {
        if (row.chnl != "Any")
          s"AND chnl='${row.chnl}' "
        else
          ""
      }
    }
    val interq2 = interq + {
      if (row.regn == "Any")
        ""
      else
        s"AND regn='${row.regn}' "

    }
    val interq3 = interq2 + {
      if (row.sku != "Any") {
        s"AND sku= '${row.sku}' "
      }
      else if (row.item != "Any")
        s"AND item= '${row.item}' "
      else {
        if (row.item_type != "Any")
          s"AND item_type= '${row.item_type}' "
        else
          ""
      }
    }
    val interq4 = interq3 + {
      if (row.subclass != "Any") {
        s"AND subclss= '${row.subclass}' "
      }
      else if (row.Class != "Any")
        s"AND clss= '${row.Class}' "
      else
        ""
    }
    val finalq = interq4 + {
      if (row.dept != "Any") {
        s"AND dept= '${row.dept}' "
      }
      else if (row.dvsn != "Any")
        s"AND dvsn= '${row.dvsn}' "
      else
        ""
    }

    val filtered={if(row.sku=="Any" && row.str=="Any")
      finalq+s"and (sku,str)>=('$lastsku','$laststr') limit $limit "
    else
      if(row.sku=="Any")
        finalq+s"and sku>='$lastsku' limit $limit "
      else {
        if(row.str=="Any")
        finalq+s"and str>='$laststr' limit $limit "
        else
          finalq+s" limit $limit "

      }
    }

    filtered + "ALLOW FILTERING;"
  }

  override def selectRows(row:salesOverAllRow,limit:Int,lastsku:String,laststr:String)= {
    var x = 0
    val ResultSet = CassandraService.executeQuery(constructQuery(row,limit,lastsku,laststr))
    var res:List[salesOverAllRowReturn]= List[salesOverAllRowReturn]()
    val ans=ResultSet.forEach(row=> {      res = res :+
      salesOverAllRowReturn(
        row.getString("str"),
        row.getString("chnl"),
        row.getString("regn"),
        row.getString("sku"),
        row.getString("item"),
        row.getString("item_type"),
        row.getString("subclss"),
        row.getString("Clss"),
        row.getString("dept"),
        row.getString("dvsn"),
        row.getInt("sales")
      )
    }
    )
    res
  }
  def getNbrOFRows(row:salesOverAllRow,limit:Int,lastsku:String,laststr:String)=
  {  var x = 0
    val ResultSet = CassandraService.executeQuery(constructQuery(row,limit,lastsku,laststr))
    ResultSet.getAvailableWithoutFetching()
  }
}






