package net.dream.erp.interfaces.service.receiving

import akka.actor.{ActorRef, ActorSystem}
import net.dream.erp.usercase.port.ReceivingAggregateFlows

class ReceivingAggregateFlowsImpl(aggregateRef: ActorRef)(
  implicit val system: ActorSystem
) extends ReceivingAggregateFlows {


}
