package net.dream.erp.run

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Frontend {
  def main(args: Array[String]): Unit = {
    // Override the configuration of the port when specified as program argument
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """)
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]"))
      .withFallback(ConfigFactory.load())

    implicit val system = ActorSystem("ClusterSystem", config)

    RestService.start
  }

}
