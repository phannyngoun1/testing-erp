package net.dream.erp.usercase.port

import akka.NotUsed
import akka.stream.scaladsl.Flow
import net.dream.erp.usercase.InventoryAggregateUseCase.Protocol._

trait InventoryAggregateFlows {

  def getInventoryEventFlow: Flow[GetInventoryRequest, GetInventoryResponse, NotUsed]

  def initialInventoryEventFlow: Flow[InitInventoryCmdRequest, InitInventoryResponse, NotUsed]

}
