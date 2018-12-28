package net.dream.erp.run

import akka.actor.ActorSystem
import net.dream.erp.integrate.RatingConsumer

object JobConsumers {

  def start(implicit system: ActorSystem) = {
    system.actorOf(RatingConsumer.props, RatingConsumer.name)
  }

}
