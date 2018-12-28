package net.dream.erp.job

import akka.actor.{Actor, ActorLogging, Props}

object TestDataReader {

  final val name = "test-data-reader"

  def props = Props(new TestDataReader)

  final case class Msg(deliveryId: Long, user: String)

  final case class Confirm(deliveryId: Long)

}

class TestDataReader extends Actor with ActorLogging {

  override def receive: Receive = ???
}
