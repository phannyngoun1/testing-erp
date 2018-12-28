package net.dream.erp.interfaces.service.part

import akka.actor.{ActorRef, Props, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId, Passivate}
import net.dream.erp.interfaces.service.part.PartService.Protocol.PartCommandRequest
import net.dream.erp.interfaces.service.sharding.ShardedServices.ShardedService

object ShardedPartService extends ShardedService {

  override def props(implicit settingRef: ActorRef): Props = Props(new ShardedPartService)
  override val shardName: String = "shard-part"
  override val extractEntityId: ExtractEntityId = {
    case cmd: PartCommandRequest =>
      (cmd.id.toString, cmd)
  }
  override val extractShardId: ExtractShardId = {
    case cmd: PartCommandRequest =>
      (cmd.id % 12).toString
  }


  case object StopPartService
}

class ShardedPartService(implicit settingRef: ActorRef) extends PartService {

  import ShardedPartService._

  override def unhandled(message: Any): Unit = message match {
    case ReceiveTimeout =>
      context.parent ! Passivate(stopMessage = StopPartService)
    case StopPartService =>
      context.stop(self)
  }

}
