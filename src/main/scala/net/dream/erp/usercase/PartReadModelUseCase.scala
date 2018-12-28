package net.dream.erp.usercase

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.{Done, NotUsed}
import net.dream.erp.domain.part.PartEvent.{NewPartEvent, PartEvent, UpdatePartEvent}
import net.dream.erp.usercase.port.{JournalReader, PartReadModelFlows}

import scala.concurrent.{ExecutionContextExecutor, Future}

class PartReadModelUseCase(partReadModelFlows: PartReadModelFlows, journalReader: JournalReader)(
  implicit val system: ActorSystem
) {

  private implicit val mat: ActorMaterializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val projectionFlow: Flow[(PartEvent, Long), Int, NotUsed] =
    Flow[(PartEvent, Long)].flatMapConcat {
      case (event: NewPartEvent, sequenceNr: Long) => Source
        .single(event.id, sequenceNr)
        .via(partReadModelFlows.addNewPartFlow)

      case (event: UpdatePartEvent, sequenceNr: Long) => Source
        .single(event.id, sequenceNr)
        .via(partReadModelFlows.updatePartFlow)
    }

  def execute(): Future[Done] = {
    partReadModelFlows.resolveLastSeqNrSource
      .flatMapConcat { lastSeqNr =>
        journalReader.eventsByTagSource(classOf[PartEvent].getName, lastSeqNr + 1)
      }
      .map { eventBody =>
        (eventBody.event.asInstanceOf[PartEvent], eventBody.sequenceNr)
      }
      .via(projectionFlow)
      .toMat(Sink.ignore)(Keep.right)
      .run()
  }

}
