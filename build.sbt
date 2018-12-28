import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

name := "Dream-ERP-System"

lazy val `erp` = project
  .in(file("."))
  .settings(multiJvmSettings: _*)
  .settings(
    organization := "net.dream",
    scalaVersion := "2.12.6",
    scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    javaOptions in run ++= Seq("-Xms128m", "-Xmx1024m", "-Djava.library.path=./target/native"),
    libraryDependencies ++= Seq(

      //Akka Http
      Library.swaggerJaxrs,
      Library.swaggerAkka,
      Library.akkaHttp,
      Library.akkaHttpSprayJson,
      Library.akkaHttpXml,
      Library.akkaHttpCors,
      Library.jaxbApi,

      //Actor system
      Library.akkaActor,
      Library.akkaStream,
      Library.akkaRemote,
      Library.akkCluster,
      Library.akkaClusterMetrics,
      Library.akkaClusterTool,
      Library.scalaTest,
      Library.akkaClusterSharding,
      Library.akkaMultiNodeTestKit,
      Library.akkaSlf4j,
      Library.akkaPersistenceQuery,

      Library.akkaCamel,
      Library.camelRabbitMQ,
      Library.alpAkka,

      //Slick
      Library.slick,
      Library.slickHikaricp,
//      Library.slickExtensions,
      Library.slickCodegen,


      //DB connector
      Library.akkaPersistenceJdbc,
      Library.levelDB,
      Library.levelDbLniAll,
      Library.mysqlConnectJava,

      Library.mssqlJdbc,

      //circe
      Library.akkaHttpCirce,
      Library.circeCore,
      Library.circeGeneric,
      Library.circeParser,
      Library.circeJava8,

      //Test kits
      Library.akkaTestKit,
      Library.akkaHttpTestKit,
      Library.akkaStreamTestKit,

      //Others
      Library.logBackClassic,
      Library.baseUnitsScala,
      Library.hashIdsScala,
      Library.enumeratum,
      Library.pureConfig,
      Library.monocleCore,
      Library.monocleMacro,
      Library.monocleLaw,
      Library.sigarLoader),

    fork in run := true,
    mainClass in(Compile, run) := Some("net.dream.erp.run.MainApp"),
    // disable parallel tests
    parallelExecution in Test := false
  )
  .configs(MultiJvm)

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)



