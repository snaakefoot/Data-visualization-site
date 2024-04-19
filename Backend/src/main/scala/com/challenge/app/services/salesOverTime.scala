package com.challenge.app.services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.challenge.app.entities.{salesOverTimePerMonth, salesOverTimePerDay}

trait salesOverTimeI {

  def getSumFromRange(start : Int, end:Int,sku:String,str:String):Int
  def getSumFromRange2(start : Int, end:Int,sku:String,str:String):Int
  def selectRowsPerDay(start : Int, end:Int,sku:String,str:String,skip:Int,limit:Int):List[salesOverTimePerDay]
  def selectRowsPermonth(start : Int, end:Int,sku:String,str:String,lastday:Int,limit:Int):List[salesOverTimePerMonth]
}



object salesOverTime  extends  salesOverTimeI {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  override def getSumFromRange(start : Int, end:Int,sku:String,str:String)={
    val startAdd=end-end%100
    val startSub=start-start%100
    val addQ=s"select sum(sales) as sum from challenge.sales where sku ='$sku' and str='$str' and datenum >=$startAdd and datenum<=$end;"
    val subQ=s"select sum(sales) as sum from challenge.sales where sku ='$sku' and str='$str' and datenum >=$startSub and datenum<$start;"
    val otherQ=s"select sum(sales) as sum from challenge.salestomonthlevel where sku ='$sku' and str='$str' and yearmonthnum >=${start/100} and yearmonthnum<=${(end/100)-1};"
    val addResult = CassandraService.executeQuery(addQ)
    val subResult = CassandraService.executeQuery(subQ)
    val MonthsResult = CassandraService.executeQuery(otherQ)
    addResult.one().getInt("sum")+subResult.one().getInt("sum")+MonthsResult.one().getInt("sum")
  }
  override def getSumFromRange2(start : Int, end:Int,sku:String,str:String)={

    val addQ=s"select sum(sales) as sum from challenge.sales where sku ='$sku' and str='$str' and datenum >=$start and datenum<=$end;"

    val ResultSet = CassandraService.executeQuery(addQ)
    ResultSet.one().getInt("sum")
  }

  override def selectRowsPerDay(start : Int, end:Int,sku:String,str:String,lastday:Int,limit:Int): List[salesOverTimePerDay] = {
    val query=s"select * from challenge.sales where sku='$sku' and str='$str' and datenum>=$lastday  and datenum<=$end limit $limit ;"
    var res:List[salesOverTimePerDay]= List[salesOverTimePerDay]()
    val ResultSet = CassandraService.executeQuery(query)
    ResultSet.forEach(row=> {      res = res :+
      salesOverTimePerDay(
        row.getString("sku"),
        row.getString("str"),
        row.getInt("datenum"),
        row.getInt("sales"),
      )
    }
    )

    res
  }
  override def selectRowsPermonth(start : Int, end:Int,sku:String,str:String,lastmonth:Int,limit:Int): List[salesOverTimePerMonth] = {
    val query=s"select * from challenge.salestomonthlevel where sku='$sku' and str='$str' and yearmonthnum>=$lastmonth  and yearmonthnum<=$end limit $limit ;"
    var res:List[salesOverTimePerMonth]= List[salesOverTimePerMonth]()
    val ResultSet = CassandraService.executeQuery(query)
    ResultSet.forEach(row=> {      res = res :+
      salesOverTimePerMonth(
        row.getString("sku"),
        row.getString("str"),
        row.getInt("yearmonthnum"),
        row.getInt("monthnum"),
        row.getInt("sales"),
      )
    }
    )

    res
  }
}






