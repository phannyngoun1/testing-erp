package net.dream.erp.interfaces.service.inventory

import akka.actor._
import akka.persistence._
import cats.implicits._
import net.dream.erp.domain.inventory.InventoryEvent.AddNewPartEvent
import net.dream.erp.domain.inventory.InventoryModel._
import net.dream.erp.domain.inventory._
import net.dream.erp.domain.location.Location
import pureconfig._

object InventoryService {

  final val serviceName = "inventory"

  def props(implicit settingRef: ActorRef): Props = Props(new InventoryService)

  def name(id: InventoryId): String = id.partId.toString

  object Protocol {

    sealed trait InventoryCommandRequest {
      val inventoryId: InventoryId
    }

    sealed trait InventoryCommandResponse {
      val inventoryId: InventoryId
    }

    sealed trait PartCommandRequest extends InventoryCommandRequest

    sealed trait InventoryCmdRequest extends InventoryCommandRequest

    sealed trait PartCommandResponse extends InventoryCommandResponse

    case class PartRequest(inventoryId: InventoryId) extends PartCommandRequest

    case class AddNewPartRequest(inventoryId: InventoryId, newPart: NewPart) extends PartCommandRequest

    case class CheckStockRequest(inventoryId: InventoryId, locRequested: Option[Location], qtyRequested: Option[Float]) extends InventoryCmdRequest

    case class GetInventoryRequest(inventoryId: InventoryId) extends InventoryCmdRequest

    case class AddNewPartSucceeded(inventoryId: InventoryId) extends PartCommandResponse

    case class AddNewPartFailed(inventoryId: InventoryId) extends PartCommandResponse

    case class CheckStockSucceeded(inventoryId: InventoryId, qyt: Float) extends InventoryCmdResponse

    case class CheckStockFailed(inventoryId: InventoryId) extends InventoryCmdResponse

    sealed trait InventoryCmdResponse extends InventoryCommandResponse

    case class GetInventorySucceeded(inventoryId: InventoryId, qty: Float) extends InventoryCmdResponse

    case class GetInventoryFailed(inventoryId: InventoryId) extends InventoryCmdResponse


    //Initial inventory
    case class InitInventoryCmdRequest(inventoryId: InventoryId) extends InventoryCommandRequest
    sealed trait InitInventoryCmdResponse extends InventoryCommandResponse
    case class InitInventorySucceed(inventoryId: InventoryId) extends InitInventoryCmdResponse
    case class InitInventoryFailed(inventoryId: InventoryId) extends InitInventoryCmdResponse


    // ---


    case class Test(test: String)

  }

  implicit class EitherOps(val self: Either[InventoryError, InventoryModel]) {
    def toSomeOrThrow: Option[InventoryModel] = self.fold(error => throw new IllegalStateException(error.message), Some(_))
  }

}

class InventoryService(implicit settingRef: ActorRef) extends PersistentActor with ActorLogging {

  import InventoryService.Protocol._
  import InventoryService._

  private val config = loadConfigOrThrow[InventoryServiceConfig](
    context.system.settings.config.getConfig("inventory.interface.inventory-aggregate ")
  )

  context.setReceiveTimeout(config.receiveTimeout)

  private var inventoryState: Option[InventoryModel] = None

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, _state: InventoryModel) =>
      inventoryState = Some(_state)
      log.info(s"Receive Recover ${_state}")
    case event: AddNewPartEvent =>
      log.info(s"Replay ${event}")
    case RecoveryCompleted =>
      log.info(s"Recovery completed: $persistenceId")
    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
  }

  override def receiveCommand: Receive = {

    case AddNewPartRequest(inventoryId, newPart) =>
      val addNewPartEvent = AddNewPartEvent(
        inventoryId,
        partNr = newPart.partNr,
        description = newPart.description,
        upc = newPart.upc,
        partTypeId = newPart.partType.value,
        uomId = newPart.uom.id,
        productInfo = None,
        initialInventory = None,
        defaultLocations = None,
        defaultVendor = None,
        defaultPartAccount = None,
        defaultProductAccount = None
      )
      persist(addNewPartEvent) { event =>
        inventoryState = applyState(event, newPart.uom).toSomeOrThrow

        //        event.initialInventory.map(stock =>
        //          persist(InventoryInitialEvent(
        //            event.inventoryId,
        //            location = stock.loc.id,
        //            uoM = stock.uoM.id,
        //            qty = stock.qty,
        //            unitCost = stock.unitCost
        //          )) { _ =>
        //            inventoryState = mapState(_.initStock(
        //              location = stock.loc,
        //              uoM = stock.uoM,
        //              qty = stock.qty,
        //              unitCost = stock.unitCost,
        //              date = stock.postDate,
        //              partTracking = stock.trackingValues.map(PartTracking(_))
        //            )).toSomeOrThrow
        //
        //          }
        //        )

        sender() ! AddNewPartSucceeded(inventoryId)
        log.info("saving snapshot")
//        saveSnapshot(inventoryState.get)

      }

    case CheckStockRequest(inventoryId, locRequested, qtyRequested) if equalsId(inventoryId) =>
      foreachState { state =>
        sender() ! CheckStockSucceeded(inventoryId, state.onHand)
      }

    case GetInventoryRequest(inventoryId) =>
      log.debug(s"GetInventoryRequest ${inventoryId}")
      sender() ! GetInventorySucceeded(inventoryId, 122)
    //      foreachState { state =>
    //
    //        log.debug(s"GetInventoryRequest ${inventoryId}")
    //        sender() ! GetInventorySucceeded(inventoryId, state.onHand)
    //      }
    case InitInventoryCmdRequest(inventoryId) =>
      log.info(s"InitInventoryCmdRequest  ${inventoryId}")
      sender() ! InitInventorySucceed(inventoryId)

  }

  override def saveSnapshot(snapshot: Any): Unit = {


    super.saveSnapshot(snapshot)
  }

  override def persistenceId: String = s"$serviceName-${self.path.name}"

  private def tryToSaveSnapshot(id: InventoryId): Unit = {
    log.info(s"tryToSaveSnapshot  --- ${inventoryState}")
    if (lastSequenceNr % config.numOfEventsToSnapshot == 0) {
    foreachState(saveSnapshot)
    }
  }

  private def foreachState(f: (InventoryModel) => Unit): Unit =
    Either.fromOption(inventoryState, InvalidStateError()).filterOrElse(!_.isActive, InvalidStateError()).foreach(f)

  private def equalsId(invId: InventoryId): Boolean =
    inventoryState match {
      case None =>
        throw new IllegalStateException(s"Invalid state: inventory Id = $invId")
      case Some(state) =>
        state.id == invId
    }

  private def applyState(event: AddNewPartEvent, uoM: UoM): Either[InventoryError, InventoryModel] = {


    val model = InventoryModel(id = event.inventoryId)
    Either.right(model)
  }

  private def mapState(
    f: (InventoryModel) => Either[InventoryError, InventoryModel]
  ): Either[InventoryError, InventoryModel] =
    for {
      state <- Either.fromOption(inventoryState, InvalidStateError())
      newState <- f(state)
    } yield newState

}
