package com.challenge.app.entities

case class salesOverAllRow (str:String,chnl:String,regn:String,sku:String,item:String,item_type:String,subclass:String,Class:String,dept:String,dvsn:String)
case class salesOverAllRowReturn (str:String,chnl:String,regn:String,sku:String,item:String,item_type:String,subclass:String,Class:String,dept:String,dvsn:String,sales:Int)
case class salesOverTimePerDay (sku:String ,str:String,datenum:Int,sales:Int)
case class salesOverTimePerMonth (sku:String ,str:String,yearmonthnum:Int,monthnum:Int,sales:Int)
