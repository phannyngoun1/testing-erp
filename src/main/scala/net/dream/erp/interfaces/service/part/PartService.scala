package net.dream.erp.interfaces.service.part

import akka.actor._
import akka.persistence._
import cats.implicits._
import net.dream.erp.domain.part.PartEvent.{DisablePartEvent, NewPartEvent, UpdatePartEvent}
import net.dream.erp.domain.part.PartModel
import net.dream.erp.domain.part.PartModel.{InvalidPartStateError, PartError}
import net.dream.erp.interfaces.service.part.PartService.Protocol._
import pureconfig._

object PartService {
  final val serviceName = "part"

  def props: Props = Props(new PartService)

  object Protocol {

    sealed trait PartCommandRequest {
      val id: Long
    }

    sealed trait PartCommandResponse {
      val id: Long
    }

    case class NewPartCommandRequest(
      override val id: Long,
      nr: String,
      description: String,
      partTypeId: Int,
      uomId: Int
    ) extends PartCommandRequest

    sealed trait NewPartResponse extends PartCommandResponse

    case class NewPartCommandSucceeded(
      override val id: Long
    ) extends NewPartResponse

    case class NewPartCommandFailed(
      override val id: Long
    ) extends NewPartResponse

    case class UpdatePartCommandRequest(
      id: Long,
      nr: String,
      description: String,
      partTypeId: Int,
      uomId: Int
    ) extends PartCommandRequest

    sealed trait UpdatePartCommandResponse extends PartCommandResponse

    case class UpdatePartCommandSucceeded(
      id: Long
    ) extends UpdatePartCommandResponse

    case class UpdatePartCommandFailed(
      id: Long
    ) extends UpdatePartCommandResponse


    case class DisablePartCommandRequest(
      id: Long
    ) extends PartCommandRequest

    sealed trait DisablePartCommandResponse extends PartCommandRequest

    case class DisablePartCommandSucceeded(id: Long) extends DisablePartCommandResponse
    case class DisablePartCommandFailed(id: Long) extends DisablePartCommandResponse

  }

  implicit class EitherOps(val self: Either[PartError, PartModel]) {
    def toSomeOrThrow: Option[PartModel] = self.fold(error => throw new IllegalStateException(error.message), Some(_))
  }

}

class PartService extends PersistentActor with ActorLogging {

  import PartService._

  private var partState: Option[PartModel] = None

  private val config = loadConfigOrThrow[PartServiceConfig](
    context.system.settings.config.getConfig("inventory.interface.inventory-aggregate")
  )

  context.setReceiveTimeout(config.receiveTimeout)

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, _state: PartModel) =>
      partState = Some(_state)
    case SaveSnapshotSuccess(metadata) =>
      log.info(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case SaveSnapshotFailure(metadata, reason) ⇒
      log.info(s"SaveSnapshotFailure: SaveSnapshotSuccess failed: $metadata, ${reason}")
    case event: NewPartEvent =>
      partState = applyState(event).toSomeOrThrow
    case event: UpdatePartEvent =>
      partState = mapState(_.updatePart(event)).toSomeOrThrow
    case RecoveryCompleted =>
      log.info(s"Recovery completed: $persistenceId")
    case _ => log.info("Other")
  }

  override def receiveCommand: Receive = {
    case part: NewPartCommandRequest =>
      val newPartEvent = NewPartEvent(part.id, part.nr, description = part.description, partTypeId = part.partTypeId, uomId = part.uomId)
      sender() ! NewPartCommandSucceeded(part.id)
      persist(newPartEvent) { event =>
        partState =  applyState(event).toSomeOrThrow
        sender() ! NewPartCommandSucceeded(part.id)
      }
    case request: UpdatePartCommandRequest if equalsId(request)=>
      persist(UpdatePartEvent(request.id, request.nr, request.description, request.partTypeId, request.uomId)) { event =>
        partState = mapState(_.updatePart(event)).toSomeOrThrow
        sender() ! UpdatePartCommandSucceeded(request.id)
        tryToSaveSnapshot(request.id)
      }

    case request: DisablePartCommandRequest =>
      persist(DisablePartEvent(request.id)) { event =>
        partState = mapState(_.disablePart()).toSomeOrThrow
        sender() ! DisablePartCommandSucceeded(event .id)
      }

    case SaveSnapshotSuccess(metadata) =>
      log.info(s"receiveCommand: SaveSnapshotSuccess succeeded: $metadata")
  }

  private def tryToSaveSnapshot(id: Long): Unit =
    if (lastSequenceNr % config.numOfEventsToSnapshot == 0) {
      foreachState(saveSnapshot)
    }


  private def applyState(event: NewPartEvent): Either[PartError, PartModel] =
    Either.right(
      PartModel(event)
    )

  private def mapState(
    f:(PartModel) => Either[PartError, PartModel]
  ): Either [PartError, PartModel]  =
    for {
      state <- Either.fromOption(partState, InvalidPartStateError())
      newState  <- f(state)
    } yield newState

  private def foreachState(f: (PartModel) => Unit): Unit =
    Either.fromOption(partState, InvalidPartStateError()).filterOrElse(_.isActive, InvalidPartStateError()).foreach(f)

  private def equalsId(request: PartCommandRequest): Boolean =
    partState match {
      case None =>
        throw new IllegalStateException(s"Invalid state: requestId = ${request.id}")
      case Some(state) =>
        state.id == request.id
    }

  override def persistenceId: String = s"$serviceName-${self.path.name}"

}
