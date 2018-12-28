package net.dream.erp.interfaces.service.setting

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SaveSnapshotSuccess, SnapshotOffer}
import net.dream.erp.domain.inventory.UoM
import net.dream.erp.domain.inventory.UoM.Measurement
import net.dream.erp.domain.uom.UoMState
import net.dream.erp.domain.uom.UoMState.UoMSettingError

object UoMSetting {

  def props = Props(new UoMSetting)

  def name = "uom-setting"

  sealed trait UoMSettingCommandRequest

  sealed trait UoMSettingCommandResponse

  case class GetUoMRequest(id: Int) extends UoMSettingCommandRequest

  case class GetUoMResponse(uoM: UoM) extends UoMSettingCommandResponse


  implicit class EitherOps(val self: Either[UoMSettingError, UoM]) {
    def toSomeOrThrow: Option[UoM] = self.fold(error => throw new IllegalStateException(error.message), Some(_))
  }

}

class UoMSetting extends PersistentActor with ActorLogging {

  import UoMSetting._

  var state: UoMState = UoMState(Set(
    UoM(
      id = 1,
      abbr = "ea",
      name = "each",
      description = "",
      measurement = Measurement.Count,
      active = true,
      readonly = true
    ),
    UoM(
      id = 2,
      abbr = "pr",
      name = "part",
      description = "",
      measurement = Measurement.Count,
      active = true,
      readonly = false
    )
  ))

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, _state: UoMState) =>
      log.info(s"restore from snapshot ${_state}")

      state = _state
    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case RecoveryCompleted =>
      log.debug(s"Recovery completed: $persistenceId")
  }

  override def persistenceId: String = "uom-setting"

  override def receiveCommand: Receive = {
    case GetUoMRequest(id) => {
      val uom = state.getUoM(id).toSomeOrThrow
      sender() ! GetUoMResponse(uom.get)
    }
  }

  private def mapState(
    f: (UoMState) => Either[UoMSettingError, UoM]
  ): Either[UoMSettingError, UoM] =
    for {
      newState <- f(state)
    } yield newState
}
