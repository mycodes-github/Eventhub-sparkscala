import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.types.{StringType, StructType}
import scala.concurrent.duration._

object EventHubHive extends App {
    val log = Logger.getLogger(getClass)
    val spark = SparkSession.builder().master("local[*]").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    log.info("starting spark structured streaming")
    var eventHubDF = spark
            .readStream
            .format("eventhubs")
            .options(EventHubUtils.eventHunConf.toMap)
            .load()
            .select('body.cast(StringType).as("word"))
            .withColumn("word", from_json('word, (new StructType).add("messageId", StringType).add("deviceId", StringType).add("temperature", StringType).add("humidity", StringType)))
            .selectExpr("word.*")

    // Stream for Every 3 seconds
    eventHubDF
            .writeStream
            .foreachBatch((ds, id) => {
                log.debug("Writing data to hive batch No-->" + id)
                ds.write.mode("append").saveAsTable("IOTdata")
                log.debug("batch No-->" + id + " completed")
            })
            .trigger(Trigger.ProcessingTime(3 seconds))
            .start()
            .awaitTermination()
}
