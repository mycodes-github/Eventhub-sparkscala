import java.io.{File, FileReader}
import java.util.Properties
import org.apache.spark.eventhubs.{ConnectionStringBuilder, EventHubsConf}

object EventHubUtils {
    val properties = new Properties
    properties.load(new FileReader(new File("src/main/resources/eventhub.properties")))
    val url = properties.getProperty("connectionString")
    val connectionString = ConnectionStringBuilder(url)
            .setEventHubName(properties.getProperty("eventHubName"))
            .build
    val eventHunConf = EventHubsConf(connectionString)

}
