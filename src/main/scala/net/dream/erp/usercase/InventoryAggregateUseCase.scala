package net.dream.erp.usercase

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import net.dream.erp.usercase.UseCaseConfig.InventoryAggregateUseCaseConfig
import net.dream.erp.usercase.port.InventoryAggregateFlows
import pureconfig._

import scala.concurrent._

object InventoryAggregateUseCase {

  object Protocol {

    sealed trait InventoryCommandRequest {
      val id: Long
    }

    sealed trait InventoryCommandResponse {
      val id: Long
    }

    case class GetInventoryRequest(override val id: Long) extends InventoryCommandRequest

    sealed trait GetInventoryResponse extends InventoryCommandResponse

    case class GetInventorySucceeded(override val id: Long, qty: Float) extends GetInventoryResponse
    case class GetInventoryFailed(override val id: Long ) extends GetInventoryResponse

    case class InitInventoryCmdRequest(
      override val id: Long,

    ) extends InventoryCommandRequest

    sealed trait InitInventoryResponse  extends InventoryCommandResponse

    case class InitInventorySucceeded(id: Long) extends InitInventoryResponse
    case class InitInventoryFailed(id: Long) extends InitInventoryResponse
  }
}

class InventoryAggregateUseCase(inventoryAggregateFlows: InventoryAggregateFlows)(implicit system: ActorSystem)
  extends UseCaseSupport {

  import InventoryAggregateUseCase.Protocol._
  import UseCaseSupport._

  implicit val mat: Materializer = ActorMaterializer()
  private val config = loadConfigOrThrow[InventoryAggregateUseCaseConfig]("inventory.use-case.inventory-use-case")
  private val bufferSize: Int = config.bufferSize

  private val getInventoryQueue: SourceQueueWithComplete[(GetInventoryRequest, Promise[GetInventoryResponse])] =
    Source.queue[(GetInventoryRequest, Promise[GetInventoryResponse])](bufferSize, OverflowStrategy.dropNew)
      .via(inventoryAggregateFlows.getInventoryEventFlow.zipPromise)
      .toMat(completePromiseSink)(Keep.left)
      .run()

  def getInventory(request: GetInventoryRequest)(implicit ec: ExecutionContext): Future[GetInventoryResponse] =
    offerToQueue(getInventoryQueue)(request, Promise())
}
