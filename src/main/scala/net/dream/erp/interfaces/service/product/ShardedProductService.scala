package net.dream.erp.interfaces.service.product

import akka.actor.{ActorRef, Props, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId, Passivate}
import net.dream.erp.interfaces.service.product.ProductService.Protocol.ProductCommandRequest
import net.dream.erp.interfaces.service.sharding.ShardedServices.ShardedService

object ShardedProductService extends ShardedService{

  override def props(implicit settingRef: ActorRef): Props = Props(new ShardedProductService)

  override val shardName: String = "shared-product"
  override val extractEntityId: ExtractEntityId = {
    case cmd: ProductCommandRequest =>
      (cmd.id.toString, cmd)
  }
  override val extractShardId: ExtractShardId = {
    case cmd: ProductCommandRequest =>
      (cmd.id % 12).toString
  }

  case object StopProductService
}

class ShardedProductService extends ProductService {

  import  ShardedProductService._

  override def unhandled(message: Any): Unit = message match {
    case ReceiveTimeout =>
      context.parent ! Passivate(stopMessage = StopProductService)
    case StopProductService =>
      context.stop(self)
  }
}