package net.dream.erp.usercase

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import net.dream.erp.domain.inventory.{NewPart, UoM}
import net.dream.erp.domain.inventory.Part.PartType
import net.dream.erp.domain.part.PartModel.PartError
import net.dream.erp.usercase.InventoryAggregateUseCase.Protocol.{InitInventoryCmdRequest, InitInventoryResponse}
import net.dream.erp.usercase.UseCaseConfig.PartAggregateUseCaseConfig
import net.dream.erp.usercase.port.{InventoryAggregateFlows, PartAggregateFlows}
import pureconfig._

import scala.concurrent._

object PartAggregateUseCase {

  object Protocol {

    sealed trait PartCommandRequest {
      val id: Long
    }

    sealed trait PartCommandResponse {
      val id: Long
    }

    case class AddNewPartRequest(
      id: Long,
      newPart: NewPart
    ) extends PartCommandRequest

    sealed trait AddNewPartResponse extends PartCommandResponse
    case class AddNewPartSucceeded(id: Long) extends AddNewPartResponse
    case class AddNewPartFailed(id: Long) extends AddNewPartResponse

    case class UpdatePartRequest(
      id: Long,
      partNr: String,
      description: String,
      upc: Option[String],
      partType: PartType,
      uom: UoM,
    ) extends PartCommandRequest

    sealed trait UpdatePartResponse extends PartCommandResponse
    case class UpdatePartSucceeded(id: Long) extends UpdatePartResponse
    case class UpdatePartFailed(id: Long, ex: PartError) extends UpdatePartResponse
  }
}

class PartAggregateUseCase(partAggregateFlows: PartAggregateFlows, inventoryAggregateFlows: InventoryAggregateFlows)(implicit system: ActorSystem)
  extends UseCaseSupport {

  import PartAggregateUseCase.Protocol._
  import UseCaseSupport._

  implicit val mat: Materializer = ActorMaterializer()
  private val config = loadConfigOrThrow[PartAggregateUseCaseConfig]("part.use-case.rating-use-case")

  private val bufferSize: Int = config.bufferSize

  val createPartFlow = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val broadcast = b.add(Broadcast[AddNewPartRequest](2))
    val zip = b.add(Zip[AddNewPartResponse, InitInventoryResponse]())

    broadcast.out(0) ~> partAggregateFlows.addPartEventFlow ~> zip.in0
    broadcast.out(1).map( rq => InitInventoryCmdRequest(rq.id)) ~> inventoryAggregateFlows.initialInventoryEventFlow ~> zip.in1

    FlowShape(broadcast.in, zip.out)
  })


  private val addNewPartQueue: SourceQueueWithComplete[(AddNewPartRequest, Promise[AddNewPartResponse])] =
    Source.queue[(AddNewPartRequest, Promise[AddNewPartResponse])](bufferSize, OverflowStrategy.dropNew)
      //.via(partAggregateFlows.addPartEventFlow.zipPromise)
      .via(createPartFlow.map(_._1).zipPromise)
      .toMat(completePromiseSink)(Keep.left)
      .run()
  private val updatePartQueue: SourceQueueWithComplete[(UpdatePartRequest, Promise[UpdatePartResponse])] =
    Source.queue[(UpdatePartRequest, Promise[UpdatePartResponse])](bufferSize, OverflowStrategy.dropNew)
    .via(partAggregateFlows.updatePartEventFlow.zipPromise)
    .toMat(completePromiseSink)(Keep.left)
    .run()








  def addNewPart(request: AddNewPartRequest)(implicit ec: ExecutionContext): Future[AddNewPartResponse] =
    offerToQueue(addNewPartQueue)(request, Promise())

  def updatePart(request: UpdatePartRequest)(implicit ec: ExecutionContext): Future[UpdatePartResponse] =
    offerToQueue(updatePartQueue)(request, Promise())

}
