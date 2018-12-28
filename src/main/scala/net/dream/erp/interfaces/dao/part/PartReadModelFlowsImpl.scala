package net.dream.erp.interfaces.dao.part

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import net.dream.erp.usercase.port.PartReadModelFlows
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class PartReadModelFlowsImpl(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
  extends PartComponent with PartReadModelFlows {
  import profile.api._

  override def addNewPartFlow: Flow[(Long, Long), Int, NotUsed] =
    Flow[(Long, Long)].mapAsync(1){
      case (id, sequenceNr) =>
        println( s"add record ${id} -- ${sequenceNr}" )

        db.run(
          PartDao += PartRecord(id, false, sequenceNr)
        )
        Future.successful(1)
    }

  override def resolveLastSeqNrSource(implicit ec: ExecutionContext): Source[Long, NotUsed] =
    Source.single(1).mapAsync(1) { _ =>
      db.run(PartDao.map(_.sequenceNr).max.result)
        .map(_.getOrElse(0L))
    }

  override def updatePartFlow: Flow[(Long, Long), Int, NotUsed] = Flow[(Long, Long)].mapAsync(1){
    case (id, sequenceNr) =>
      println( s"update record ${id} -- ${sequenceNr}" )
      Future.successful(1)
//      db.run(
//        PartDao
//          .filter(_.id === id)
//          .map(e => (e.sequenceNr))
//          .update(sequenceNr)
//      )
  }
}
