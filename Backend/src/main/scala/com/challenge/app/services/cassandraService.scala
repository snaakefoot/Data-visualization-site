package com.challenge.app.services

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.ResultSet
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import java.net.InetSocketAddress
import scala.util.Properties
trait CassandraServiceI {
  def getPassword(email:String):String
  def executeQuery(query: String): ResultSet
  def addUser(email:String ,password: String):ResultSet
  def initialize(): Unit
  def checkAccount(email : String):Boolean
}

object CassandraService extends CassandraServiceI {

  val cassandraEndpoint =Properties.envOrElse("CASSANDRA_ENDPOINT", "localhost")
  val session: CqlSession = CqlSession.builder()
    .addContactPoint(new InetSocketAddress(cassandraEndpoint, 9042))
    .withLocalDatacenter("datacenter1")
    .build()

  override def getPassword(email:String) = {

    val res=session.execute(s"select * from challenge.users where email='${email}' ;")
    val hasRows = res.iterator().hasNext

    if(hasRows)
      res.one().getString("password")
    else
      "*"
  }
  override def executeQuery(query: String): ResultSet = {
    val statement = SimpleStatement.builder(query)
      // Set the fetch size
      .setPageSize(100000000) // Set your desired fetch size
      .build()
    val resultSet = session.execute(statement) // Execute the query
    // Process the result set, print for example

    resultSet
  }
  override def addUser(email: String, password: String): ResultSet ={

    val resultSet =session.execute(s"INSERT INTO challenge.users (email,id,password)  VALUES( '${email}',uuid(),'${password}')")

    resultSet
  }

  override  def initialize(): Unit = {
      try {
        // Create namespace (keyspace) named "challenge"
        val createNamespaceCql = "CREATE KEYSPACE IF NOT EXISTS challenge WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};"
        session.execute(createNamespaceCql)
        val createTableCql1 = "CREATE TABLE IF NOT EXISTS Challenge.Users (Email text,id uuid,password text,PRIMARY KEY (email, id));"
        val createTableCql2="CREATE TABLE IF NOT EXISTS Challenge.merged_salesOverAll ( sku TEXT,   str TEXT,  sales INT,   regn TEXT,  chnl TEXT,   cntry TEXT,  item TEXT,   item_type TEXT,   subclss TEXT,   clss TEXT,    dept TEXT,   dvsn TEXT,    cmpy TEXT,    PRIMARY KEY ((cmpy,cntry), sku, str));"
        val createTableCql3="CREATE TABLE IF NOT EXISTS CHALLENGE.SALESTOMONTHLEVEL(sku text,str text, yearmonthnum int , monthnum int,sales int, , primary key ((sku,str),yearmonthnum,monthnum));"
        val createTableCql4="CREATE TABLE IF NOT EXISTS Challenge.sales ( sku TEXT, str TEXT,  datenum INT, sales INT,   PRIMARY KEY ((sku, str), datenum));"

        session.execute(createTableCql1)
        session.execute(createTableCql2)
        session.execute(createTableCql3)
        session.execute(createTableCql4)

        println("Namespace and table created successfully.")
      }
    }
  override  def checkAccount(email : String):Boolean={

    val res=session.execute(s"select * from challenge.users where email='${email}' ;")
    val hasRows = res.iterator().hasNext

    hasRows
  }
}