package net.dream.erp.usercase.port

import akka.NotUsed
import akka.stream.scaladsl.Flow
import net.dream.erp.usercase.PartAggregateUseCase.Protocol._

trait PartAggregateFlows {

  def addPartEventFlow: Flow[AddNewPartRequest, AddNewPartResponse, NotUsed]
  def updatePartEventFlow: Flow[UpdatePartRequest, UpdatePartResponse, NotUsed]
}
