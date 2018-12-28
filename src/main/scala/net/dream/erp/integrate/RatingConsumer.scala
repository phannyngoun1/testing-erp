package net.dream.erp.integrate

import akka.actor._
import akka.stream.alpakka.amqp.IncomingMessage
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink}
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.util.ByteString
import io.circe.parser.decode
import net.dream.erp.integrate.JsonSerialize._
import net.dream.erp.rest.hello.HelloActor.Greeting

object RatingConsumer extends {
  def props = Props(new RatingConsumer)
  def name = "rating-consumer"

  def queue = "rating-data-fetched"
}

class RatingConsumer extends Actor with ActorLogging with ConsumerSupport {

  import RatingConsumer._
  override def queueName: String = queue

  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.system.dispatcher

  override def preStart(): Unit = {

    val flow1 = Flow[IncomingMessage].map(msg => msg.bytes)
    val flow2 = Flow[ByteString].map(_.utf8String)
    val flow3 = Flow[String].map(v => decode[Greeting](v).getOrElse(Greeting("Ooop")))
    val sink = Sink.foreach[Greeting](  self ! _ )

    val graph = RunnableGraph.fromGraph(GraphDSL.create(sink) { implicit builder =>
      s =>
        import GraphDSL.Implicits._
        amqpSource ~> flow1 ~> flow2 ~> flow3 ~> s.in
        ClosedShape
    })
    val future = graph.run()
    future.onComplete(_.foreach(_ =>
      println("receive")
    ))
  }

  override def receive: Receive = {
    case Greeting(greeting) => log.info(s"get greeting event ${greeting}")
  }
}