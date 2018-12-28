package net.dream.erp.readsides.operdb


import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import net.dream.erp.interfaces.dao.part.PartReadModelFlowsImpl
import net.dream.erp.interfaces.readjournal.JournalReaderImpl
import net.dream.erp.usercase.PartReadModelUseCase
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

object ReadSideApp extends App {
  val rootConfig      = ConfigFactory.load()
  implicit val system = ActorSystem("ClusterSystem", config = rootConfig)
  val dbConfig        = DatabaseConfig.forConfig[JdbcProfile](path = "slick", rootConfig)

  new PartReadModelUseCase(new PartReadModelFlowsImpl(dbConfig.profile,  dbConfig.db), new JournalReaderImpl())
      .execute()

  sys.addShutdownHook {
    system.terminate()
  }
}
