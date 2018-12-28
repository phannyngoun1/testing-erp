package net.dream.erp.integrate

import akka.actor.ActorContext
import akka.stream.alpakka.amqp.{NamedQueueSourceSettings, QueueDeclaration}
import akka.stream.alpakka.amqp.javadsl.AmqpSource

trait ConsumerSupport extends ConnectionProvider {
  def context: ActorContext
  def queueName: String

  def amqpSource = {
    val qName = s"amqp-${queueName}"

    val queueDeclaration = QueueDeclaration(qName)

    AmqpSource.atMostOnceSource(
      NamedQueueSourceSettings(connectionProvider, qName).
        withDeclarations(queueDeclaration), bufferSize)
  }

  def bufferSize: Int = 10
}
