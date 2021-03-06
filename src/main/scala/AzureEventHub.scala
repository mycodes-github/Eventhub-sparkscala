import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.types.{StringType, StructType}
import scala.concurrent.duration._

object AzureEventHub extends App {

    val spark = SparkSession.builder().master("local[*]").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    var eventHubDF = spark
            .readStream
            .format("eventhubs")
            .options(EventHubUtils.eventHunConf.toMap)
            .load()
            .select('body.cast(StringType).as("word"))
            .withColumn("word", from_json('word, (new StructType).add("messageId", StringType).add("deviceId", StringType).add("temperature", StringType).add("humidity", StringType)))
            .selectExpr("word.*")

    // Stream for Every 3 seconds
    eventHubDF//.groupBy('word)
            ///.agg(count('word).as("count"))
            .writeStream
            .format("console")
            .outputMode(OutputMode.Append())
            .trigger(Trigger.ProcessingTime(3 seconds))
            .start()
            .awaitTermination()
}
