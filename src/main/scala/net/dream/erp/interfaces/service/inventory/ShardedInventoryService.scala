package net.dream.erp.interfaces.service.inventory

import akka.actor._
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import net.dream.erp.domain.inventory.InventoryId
import net.dream.erp.interfaces.service.inventory.InventoryService.Protocol.InventoryCommandRequest
import net.dream.erp.interfaces.service.sharding.ShardedServices.ShardedService

object ShardedInventoryService extends ShardedService{

  def props(implicit settingRef: ActorRef): Props = Props(new ShardedInventoryService)

  def name(id: InventoryId): String = id.partId.toString

  val shardName = "sharded-inventory"

  case object StopInventoryService

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: InventoryCommandRequest =>
      (cmd.inventoryId.partId.toString, cmd)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case cmd: InventoryCommandRequest =>
      (cmd.inventoryId.partId % 12).toString
  }

}

class ShardedInventoryService(implicit settingRef: ActorRef) extends InventoryService {
  import ShardedInventoryService._

  override def unhandled(message: Any): Unit = message match {
    case ReceiveTimeout =>
      context.parent ! Passivate(stopMessage = StopInventoryService)
    case StopInventoryService =>
      context.stop(self)
  }
}
