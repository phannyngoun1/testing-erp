package net.dream.erp.rest.hello

import akka.actor.{Actor, ActorLogging}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import net.dream.erp.integrate.ProducerSupport

object HelloActor {

  case class Hello(name: String)

  case class Greeting(greeting: String)

  case object AnonymousHello

}

class HelloActor extends Actor with ActorLogging with ProducerSupport {

  import HelloActor._
  import io.circe.syntax._
  import net.dream.erp.integrate.JsonSerialize._


  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.system.dispatcher

  def receive: Receive = {
    case AnonymousHello => {
      sender ! Greeting("Hello")
    }
    case Hello(name) =>
      Source.single(Greeting(name).asJson.noSpaces)
        .map(s => ByteString(s)).runWith(amqpSink)
      sender ! Greeting(s"Hello, $name")
  }

  override def queueName: String = "rating-data-fetched"
}