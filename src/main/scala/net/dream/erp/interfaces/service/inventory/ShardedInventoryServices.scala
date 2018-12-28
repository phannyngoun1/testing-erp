package net.dream.erp.interfaces.service.inventory

import akka.actor._
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import net.dream.erp.interfaces.service.inventory.InventoryService.Protocol.{InventoryCommandRequest, PartRequest}

object ShardedInventoryServices {

  def props(implicit settingRef: ActorRef): Props = Props(new ShardedInventoryServices)
  def name: String = "sharded-inventory-services"

  def start(system: ActorSystem)(implicit  settingRef: ActorRef): ActorRef = {

    system.log.debug("ShardedInventory#start: start")
    val actorRef = ClusterSharding(system).start(
      ShardedInventoryService.shardName,
      ShardedInventoryService.props,
      ClusterShardingSettings(system),
      ShardedInventoryService.extractEntityId,
      ShardedInventoryService.extractShardId
    )
    system.log.debug("ShardedInventory#start: finish")
    actorRef
  }

  def shardRegion(system: ActorSystem): ActorRef =
    ClusterSharding(system).shardRegion(ShardedInventoryService.shardName)

}


class ShardedInventoryServices(implicit settingRef: ActorRef) extends Actor with ActorLogging {

  ShardedInventoryServices.start(context.system)

  override def receive: Receive = {
    case cmd: InventoryCommandRequest =>
      ShardedInventoryServices.shardRegion(context.system) forward cmd
  }
}