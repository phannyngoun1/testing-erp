package net.dream.erp.interfaces.service.inventory

import akka.NotUsed
import akka.actor._
import akka.pattern.ask
import akka.stream.scaladsl._
import akka.util.Timeout
import net.dream.erp.domain.inventory.InventoryId
import net.dream.erp.interfaces.service.part.PartAggregateFlowsConfig
import net.dream.erp.usercase.InventoryAggregateUseCase
import net.dream.erp.usercase.port.InventoryAggregateFlows
import pureconfig._

class InventoryAggregateFlowsImpl(aggregateRef: ActorRef)
  (implicit val system: ActorSystem) extends InventoryAggregateFlows  {

  private val config = loadConfigOrThrow[PartAggregateFlowsConfig](
    system.settings.config.getConfig("inventory.interface.inventory-aggregate-flows")
  )

  private implicit val to: Timeout = Timeout(config.callTimeout)

  override def getInventoryEventFlow: Flow[InventoryAggregateUseCase.Protocol.GetInventoryRequest, InventoryAggregateUseCase.Protocol.GetInventoryResponse, NotUsed] =
    Flow[InventoryAggregateUseCase.Protocol.GetInventoryRequest]
    .map { request =>
      InventoryService.Protocol.GetInventoryRequest(InventoryId(request.id))
    }.mapAsync(1)(aggregateRef ? _)
    .map {
      case response: InventoryService.Protocol.GetInventorySucceeded =>
        InventoryAggregateUseCase.Protocol.GetInventorySucceeded(response.inventoryId.partId, response.qty)
      case response: InventoryService.Protocol.GetInventoryFailed =>
        InventoryAggregateUseCase.Protocol.GetInventoryFailed(response.inventoryId.partId)
    }

  override def initialInventoryEventFlow: Flow[InventoryAggregateUseCase.Protocol.InitInventoryCmdRequest, InventoryAggregateUseCase.Protocol.InitInventoryResponse, NotUsed] =
    Flow[InventoryAggregateUseCase.Protocol.InitInventoryCmdRequest]
    .map{ request =>
      InventoryService.Protocol.InitInventoryCmdRequest(InventoryId(request.id))
    }.mapAsync(1)(aggregateRef? _)
    .map {
      case response: InventoryService.Protocol.InitInventorySucceed => InventoryAggregateUseCase.Protocol.InitInventorySucceeded(response.inventoryId.partId)
      case response: InventoryService.Protocol.InitInventoryFailed => InventoryAggregateUseCase.Protocol.InitInventoryFailed(response.inventoryId.partId)
    }
}
