package net.dream.erp.integrate

import akka.stream.alpakka.amqp.AmqpDetailsConnectionProvider

trait ConnectionProvider {
  val connectionProvider =
    AmqpDetailsConnectionProvider(List(("invalid", 5673))).withHostsAndPorts(("localhost", 5672))
}
