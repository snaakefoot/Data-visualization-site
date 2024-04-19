package com.challenge.app.services

import scala.util.Properties
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, DataFrame}
trait sparkServiceI {
  def RunCql(query :String,table:String): String
  def testing()
  def loading(table:String)
}
object sparkService extends sparkServiceI{

  val cassandraEndpoint =Properties.envOrElse("CASSANDRA_ENDPOINT", "localhost")
  val conf = new SparkConf()
    .setAppName("AkkaSparkCassandraIntegration")
    .setMaster("local[*]") // or your Spark cluster URL
    .set("spark.cassandra.connection.host", cassandraEndpoint+":9042")

  override def RunCql(query :String,table:String): String = {
    val spark = SparkSession.builder()
      .config(conf)
      .getOrCreate()
    println("im in")
    println("running the query")
    try{
      val resultDF: DataFrame = spark.sql(query)
      val jsonResult: String = resultDF.toJSON.collect().mkString("[", ",", "]")
      println(jsonResult)

    }
    catch {
      case e => println(e)
    }
    println("out of the query")

    spark.stop()
    ""
  }
  override def testing(){
    val spark = SparkSession.builder()
    .config(conf)
    .getOrCreate()
    val df = spark.read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "salestomonthlevel", "keyspace" -> "challenge"))
      .load()

    df.show()
    spark.stop()
  }
  override def loading(table:String){
    // Create SparkSession
    val spark = SparkSession.builder()
      .appName("CsvToCassandra")
      .config(conf)
      .getOrCreate()
    println(s"starting to load $table")
    // Read CSV file into DataFrame
    val df = spark.read
      .format("csv")
      .option("header", "true")
      .load(s"src/main/resources/$table.csv")
    println("loading ...")
    df.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> table, "keyspace" -> "challenge","confirm.truncate" -> "true" ))
      .mode("overwrite") // or overwrite, depending on your use case
      .save()
    println(s" Done ! loading of $table table complete ")
    // Stop SparkSession
    spark.stop()
  }

}