package net.dream.erp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import net.dream.erp.interfaces.dao.rating.RatingSourceReadModelFlowsImpl
import net.dream.erp.usercase.rating.RatingSourceReadModelUseCase
import net.dream.erp.usercase.rating.RatingSourceReadModelUseCase.Protocol.FetchRatingRequest
import test.models.DB

import scala.concurrent.ExecutionContextExecutor

object PlayerRatingSync extends App {

  implicit val system = ActorSystem("HelloActorSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher


  val flow = new RatingSourceReadModelFlowsImpl(DB.dbProfile, DB.db)

  flow.fetchRatingSource.runForeach(println)

  val test = new RatingSourceReadModelUseCase(flow)

  println("call fetch rating")
  val result = test.fetchRating(FetchRatingRequest(1l)) map { res =>
    println(s" Fetched size ${res.size} ")
  }

  println("End fetch rating")

  result.onComplete(_ => {
    println("Completed")
  })

}
