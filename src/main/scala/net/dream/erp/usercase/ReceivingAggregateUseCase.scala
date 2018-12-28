package net.dream.erp.usercase

import akka.actor.ActorSystem
import net.dream.erp.usercase.port.ReceivingAggregateFlows

object ReceivingAggregateUseCase {

  object protocol {

  }

}

class ReceivingAggregateUseCase(receivingAggregateFlows: ReceivingAggregateFlows)(implicit system: ActorSystem)
  extends UseCaseSupport {

}
