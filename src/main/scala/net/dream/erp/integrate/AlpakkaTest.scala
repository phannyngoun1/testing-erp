package net.dream.erp.integrate


object AlpakkaTest extends App {

//  implicit val system = ActorSystem("coffee")
  //  implicit val materializer = ActorMaterializer()
  //  implicit val executionContext = system.dispatcher
  //
  //  val queueName = "amqp-rating-data-fetched"
  //  val queueDeclaration = QueueDeclaration(queueName)
  //
  //  //  val connectionProvider = AmqpLocalConnectionProvider
  //  val connectionProvider =
  //    AmqpDetailsConnectionProvider(List(("invalid", 5673))).withHostsAndPorts(("localhost", 5672))
  //
  //  val amqpSource = AmqpSource.atMostOnceSource(
  //    NamedQueueSourceSettings(connectionProvider, queueName).withDeclarations(queueDeclaration),
  //    bufferSize = 10
  //  )
  //
  //  val flow1 = Flow[IncomingMessage].map(msg => msg.bytes)
  //  val flow2 = Flow[ByteString].map(_.utf8String)
  //  //val flow3 = Flow[String].map(v => decode[Greeting](v).getOrElse(Greeting("Ooop")))
  //  val sink = Sink.foreach[String](println)
  //
  //  val graph = RunnableGraph.fromGraph(GraphDSL.create(sink) { implicit builder =>
  //    s =>
  //      import GraphDSL.Implicits._
  //      amqpSource ~> flow1 ~> flow2 ~> s.in
  //      ClosedShape
  //  })
  //
  //  val future = graph.run()
  //  future.onComplete { _ =>
  //    println("Complete")
  //  }
}

