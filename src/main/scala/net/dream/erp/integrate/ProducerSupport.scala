package net.dream.erp.integrate

import akka.actor.ActorContext
import akka.stream.alpakka.amqp.scaladsl.AmqpSink
import akka.stream.alpakka.amqp.{AmqpSinkSettings, QueueDeclaration}

trait ProducerSupport extends ConnectionProvider {
  def context: ActorContext

  def queueName: String

  def amqpSink = {
    val qName: String = s"amqp-${queueName}"
    val queueDeclaration = QueueDeclaration(qName)

    val amqpSink = AmqpSink.simple(
      AmqpSinkSettings(connectionProvider)
        .withRoutingKey(qName)
        .withDeclarations(queueDeclaration)
    )
    amqpSink
  }


}
