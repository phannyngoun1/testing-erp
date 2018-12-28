package net.dream.erp.interfaces.service.sharding

import akka.actor._
import akka.cluster.sharding._
import net.dream.erp.interfaces.service.inventory.InventoryService.Protocol.InventoryCommandRequest
import net.dream.erp.interfaces.service.inventory.ShardedInventoryService
import net.dream.erp.interfaces.service.part.PartService.Protocol.PartCommandRequest
import net.dream.erp.interfaces.service.part.ShardedPartService
import net.dream.erp.interfaces.service.product.ProductService.Protocol.ProductCommandRequest
import net.dream.erp.interfaces.service.product.ShardedProductService

object ShardedServices {

  def props(implicit settingRef: ActorRef): Props = Props(new ShardedServices)

  def name: String = "sharded-services"

  trait ShardedService {
    def props(implicit settingRef: ActorRef): Props

    val shardName: String
    val extractEntityId: ShardRegion.ExtractEntityId
    val extractShardId: ShardRegion.ExtractShardId
  }

  def start(services: List[ShardedService], system: ActorSystem)(implicit settingRef: ActorRef) = {
    services.map(service =>
      ClusterSharding(system).start(
        typeName = service.shardName,
        entityProps = service.props,
        settings = ClusterShardingSettings(system),
        extractEntityId = service.extractEntityId,
        extractShardId = service.extractShardId
      )
    )
  }

  def shardRegion(system: ActorSystem, shardedService: ShardedService): ActorRef =
    ClusterSharding(system).shardRegion(shardedService.shardName)

}

class ShardedServices(implicit settingRef: ActorRef) extends Actor with ActorLogging {

  val services = List(
    ShardedPartService,
    ShardedInventoryService
//    ShardedProductService
  )
  ShardedServices.start(services, context.system)

  override def receive: Receive = {
    case cmd: PartCommandRequest =>
      ShardedServices.shardRegion(context.system, ShardedPartService) forward cmd
    case cmd: InventoryCommandRequest =>
      ShardedServices.shardRegion(context.system, ShardedInventoryService) forward cmd
    case cmd: ProductCommandRequest =>
      ShardedServices.shardRegion(context.system, ShardedProductService) forward cmd
  }
}
