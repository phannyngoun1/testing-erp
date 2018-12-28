package net.dream.erp.interfaces.service.product

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object ProductService {

  object Protocol {

    sealed trait ProductCommandRequest {
      val id: Long
    }

    sealed trait ProductCommandResponse {
      val id: Long
    }

  }

}

class ProductService extends PersistentActor with ActorLogging {
  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = ???

  override def persistenceId: String = ???
}
