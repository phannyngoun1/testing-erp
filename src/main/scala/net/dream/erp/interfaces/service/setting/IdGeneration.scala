package net.dream.erp.interfaces.service.setting

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SaveSnapshotSuccess, SnapshotOffer}
import enumeratum._
import net.dream.erp.interfaces.service.setting.IdGeneration.{GetNextIdRequest, GetNextIdResponse}
import net.dream.erp.interfaces.service.setting.IdsState.NextIdEvent

object IdGeneration {

  def props = Props(new IdGeneration)

  def name = "idGeneration"

  sealed trait IdGenType extends EnumEntry

  object SettingType extends Enum[IdGenType] {
    val values = findValues
    case object PartIdGenType extends IdGenType

  }

  sealed trait IdGenCommandRequest

  sealed trait IdRenResponse

  case class GetNextIdRequest(idGenType: IdGenType) extends IdGenCommandRequest

  case class GetNextIdResponse(id: Long) extends IdRenResponse

  case class Snapshot(
    ids: Map[String, Long]
  )

}

class IdGeneration extends PersistentActor with ActorLogging {

  import IdGeneration._
  var idsState = IdsState.empty

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, _state: Snapshot) =>
      log.info(s"restore from snapshot ${_state}")
      idsState = IdsState.applyState(_state.ids.map(f =>  SettingType.withName(f._1) -> f._2))
    case event: NextIdEvent =>
      idsState = idsState.nextId(event)
    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case RecoveryCompleted =>
      log.debug(s"Recovery completed: $persistenceId")
  }

  override def receiveCommand: Receive = {
    case GetNextIdRequest(idGenType) =>
      persist(idsState.nextIdEvent(idGenType)) { event =>
        idsState = idsState.nextId(event)
        sender() ! GetNextIdResponse(event.nextId)
        if (lastSequenceNr % 20 == 0)
          saveSnapshot(Snapshot(idsState.getIds.map(f => f._1.entryName -> f._2)))

      }
  }

  override def persistenceId: String = "id-generation"
}
