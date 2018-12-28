package net.dream.erp.interfaces.service.part

import akka.NotUsed
import akka.actor._
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import net.dream.erp.domain.part.PartModel.DefaultPartError
import net.dream.erp.interfaces.service.part.PartService.Protocol.{UpdatePartCommandFailed, UpdatePartCommandRequest, UpdatePartCommandSucceeded}
import net.dream.erp.usercase.PartAggregateUseCase.Protocol
import net.dream.erp.usercase.PartAggregateUseCase.Protocol.{UpdatePartFailed, UpdatePartSucceeded}
import net.dream.erp.usercase._
import net.dream.erp.usercase.port.PartAggregateFlows
import pureconfig._


class PartAggregateFlowsImpl(aggregateRef: ActorRef)(
  implicit val system: ActorSystem
) extends PartAggregateFlows {


  private val config = loadConfigOrThrow[PartAggregateFlowsConfig](
    system.settings.config.getConfig("inventory.interface.inventory-aggregate-flows")
  )

  private implicit val to: Timeout = Timeout(config.callTimeout)

  override def addPartEventFlow: Flow[PartAggregateUseCase.Protocol.AddNewPartRequest, PartAggregateUseCase.Protocol.AddNewPartResponse, NotUsed] =
    Flow[PartAggregateUseCase.Protocol.AddNewPartRequest]
      .map { request =>
        PartService.Protocol.NewPartCommandRequest(
          request.id,
          request.newPart.partNr,
          request.newPart.description,
          request.newPart.partType.value,
          request.newPart.uom.id
        )
      }.mapAsync(1)(aggregateRef ? _ )
      .map {
        case response: PartService.Protocol.NewPartCommandSucceeded =>
          PartAggregateUseCase.Protocol.AddNewPartSucceeded(response.id)
        case response: PartService.Protocol.NewPartCommandFailed =>
          PartAggregateUseCase.Protocol.AddNewPartFailed(response.id)
      }

  override def updatePartEventFlow: Flow[Protocol.UpdatePartRequest, Protocol.UpdatePartResponse, NotUsed] = {
    Flow[Protocol.UpdatePartRequest]
      .map { request =>
        UpdatePartCommandRequest(
          id          = request.id,
          nr          = request.partNr,
          description = request.description,
          partTypeId  = request.partType.value,
          uomId       = request.uom.id
        )
      }.mapAsync(1)(aggregateRef ? _ )
      .map {
        case response: UpdatePartCommandSucceeded =>
          UpdatePartSucceeded(response.id)
        case response: UpdatePartCommandFailed=>
          UpdatePartFailed(response.id, DefaultPartError("Error"))
        case ex : Exception =>
          UpdatePartFailed(0, DefaultPartError(ex.getMessage))
      }
  }

}
