package net.dream.erp.run

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import net.dream.erp.interfaces.dao.part.PartReadModelFlowsImpl
import net.dream.erp.interfaces.readjournal.JournalReaderImpl
import net.dream.erp.interfaces.service.setting.SettingRouter
import net.dream.erp.interfaces.service.sharding.ShardedServices
import net.dream.erp.readsides.operdb.ReadSideApp.{dbConfig, rootConfig, system}
import net.dream.erp.usercase.PartReadModelUseCase
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

object ReadSide {
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
    val dbConfig        = DatabaseConfig.forConfig[JdbcProfile](path = "slick", config)

    system.actorOf(ShardedServices.props, ShardedServices.name)

    new PartReadModelUseCase(new PartReadModelFlowsImpl(dbConfig.profile,  dbConfig.db), new JournalReaderImpl())
      .execute()

    sys.addShutdownHook {
      system.terminate()
    }
  }
}
