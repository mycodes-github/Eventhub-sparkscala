name := "Eventhub-sparkscala"

version := "0.1"

scalaVersion := "2.11.11"


val sparkVersion = "2.4.5"
val eventHub = "2.3.10"

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,
    "com.microsoft.azure" %% "azure-eventhubs-spark" % eventHub
)