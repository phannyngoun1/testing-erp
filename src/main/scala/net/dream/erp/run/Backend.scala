package net.dream.erp.run

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import net.dream.erp.interfaces.service.setting.SettingRouter
import net.dream.erp.interfaces.service.sharding.ShardedServices

object Backend {

  def main(args: Array[String]): Unit = {
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """)
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(ConfigFactory.load())

    implicit val system = ActorSystem("ClusterSystem", config)
    implicit val settingRouter = system.actorOf(SettingRouter.props, SettingRouter.name)
    system.actorOf(ShardedServices.props, ShardedServices.name)
  }

}
