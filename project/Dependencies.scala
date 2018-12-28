import sbt._

object Version {
  final val Akka = "2.5.14"
  final val AkkaHttp = "10.1.3"
  final val AkkaPersistenceCassandra = "0.86"
  final val AkkaLog4j = "1.6.1"
  final val AkkaHttpJson = "1.21.0"
  final val Circe = "0.9.3"
  final val CamelRabbitMQ = "2.22.0"
  final val Slick = "3.2.3"
  //final val SlickExtensions = "3.0.0"
  final val HikariCP = "3.2.0"
  final val MariaDB = "1.4.6"
  final val Log4j = "2.6"
  final val SwaggerAkka = "0.14.1"
  final val SwaggerJaxrs = "1.5.20"
  final val ScalaCheck = "1.14.0"
  final val ScalaTest = "3.0.5"
  final val Scala = "2.12.6"
  final val AkkaHttpCors = "0.3.0"
  final val JaxbApi= "2.3.0"
  final val Slf4jSimple = "1.7.25"
  final val Logback = "1.0.10"
  final val  AkkaPersistenceJdbc = "3.4.0"
  final val LevelDb = "0.7"
  final val LevelDbLniAll = "1.8"
  final val MysqlConnectorJava = "5.1.42"
  final val MssqlJdbc = "6.2.1.jre8"

  final val BaseUnitsScala = "0.1.21"
  final val HashIdsScala =  "1.3"
  final val Enumeratum = "1.5.13"
  final val PureConfig = "0.9.0"
  final val Monocle = "1.5.0"
  final val SigarLoader = "1.6.6"
  final val AlpAkka = "0.20"
}

object Library {
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % Version.AkkaHttp
  val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % Version.AkkaHttp
  val akkaHttpXml = "com.typesafe.akka" %% "akka-http-xml"        % Version.AkkaHttp

  val akkaRemote =  "com.typesafe.akka" %% "akka-remote" % Version.Akka
  val akkCluster = "com.typesafe.akka" %% "akka-cluster" % Version.Akka
  val akkaClusterMetrics =  "com.typesafe.akka" %% "akka-cluster-metrics" % Version.Akka
  val akkaClusterTool = "com.typesafe.akka" %% "akka-cluster-tools" % Version.Akka
  val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % Version.Akka
  val akkaMultiNodeTestKit =  "com.typesafe.akka" %% "akka-multi-node-testkit" % Version.Akka
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % Version.Akka
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.Akka
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.Akka
  val akkaPersistenceQuery = "com.typesafe.akka" %% "akka-persistence-query" % Version.Akka

  val akkaHttpCors = "ch.megard" %% "akka-http-cors" % Version.AkkaHttpCors
  val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % Version.Akka
  val akkaPersistenceCassandra = "com.typesafe.akka" %% "akka-persistence-cassandra" % Version.AkkaPersistenceCassandra
  val akkaCamel = "com.typesafe.akka" %% "akka-camel" % Version.Akka
  val camelRabbitMQ = "org.apache.camel" % "camel-rabbitmq" % Version.CamelRabbitMQ
  val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % Version.AkkaHttpJson

  val circeCore = "io.circe" %% "circe-core" % Version.Circe
  val circeGeneric = "io.circe" %% "circe-generic" % Version.Circe
  val circeParser = "io.circe" %% "circe-parser" % Version.Circe
  val circeJava8 = "io.circe" %% "circe-java8" % Version.Circe

  val slick = "com.typesafe.slick" %% "slick" % Version.Slick
  val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp"  % Version.Slick
  //val slickExtensions =  "com.typesafe.slick" %% "slick-extensions" % Version.SlickExtensions
  val slickCodegen = "com.typesafe.slick" %% "slick-codegen" % Version.Slick

  val akkaPersistenceJdbc = "com.github.dnvriend" %% "akka-persistence-jdbc" % Version.AkkaPersistenceJdbc
  val levelDB = "org.iq80.leveldb" % "leveldb" % Version.LevelDb
  val levelDbLniAll = "org.fusesource.leveldbjni" % "leveldbjni-all" % Version.LevelDbLniAll

  val mysqlConnectJava = "mysql" % "mysql-connector-java" % Version.MysqlConnectorJava
  val mssqlJdbc = "com.microsoft.sqlserver" % "mssql-jdbc" % Version.MssqlJdbc

  val hikariCP = "com.zaxxer" % "HikariCP" % Version.HikariCP
  val mariaDb = "org.mrariadb.jdbc" % "mariadb-java-client" % Version.MariaDB
  val swaggerAkka = "com.github.swagger-akka-http" %% "swagger-akka-http" % Version.SwaggerAkka
  val swaggerJaxrs = "io.swagger" % "swagger-jaxrs" % Version.SwaggerJaxrs
  val akkaLog4j = "de.heikoseeberger" %% "akka-log4j" % Version.AkkaLog4j

  val log4jCore = "org.apache.logging.log4j" % "log4j-core" % Version.Log4j
  val slf4jLog4jBridge = "org.apache.logging.log4j" % "log4j-slf4j-impl" % Version.Log4j
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.ScalaCheck
  val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest

  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % Version.Akka
  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % Version.AkkaHttp
  val akkaStreamTestKit = "com.typesafe.akka" %% "akka-stream-testkit"  %  Version.Akka % Test

  val jaxbApi = "javax.xml.bind" % "jaxb-api" % Version.JaxbApi
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % Version.Slf4jSimple

  val logBackClassic = "ch.qos.logback" % "logback-classic" % Version.Logback

  val baseUnitsScala = "org.sisioh" %% "baseunits-scala" % Version.BaseUnitsScala
  val hashIdsScala = "com.github.ancane" %% "hashids-scala" % Version.HashIdsScala
  val enumeratum = "com.beachape" %% "enumeratum" % Version.Enumeratum

  val pureConfig = "com.github.pureconfig" %% "pureconfig" % Version.PureConfig
  val monocleCore = "com.github.julien-truffaut" %% "monocle-core"  % Version.Monocle
  val monocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % Version.Monocle
  val monocleLaw = "com.github.julien-truffaut" %% "monocle-law"   % Version.Monocle

  val alpAkka = "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % Version.AlpAkka

  val sigarLoader = "io.kamon" % "sigar-loader" % Version.SigarLoader
}
