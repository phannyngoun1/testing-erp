package net.dream.erp.usercase.rating

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Keep, Source, SourceQueueWithComplete}
import net.dream.erp.usercase.UseCaseSupport
import net.dream.erp.usercase.rating.port.RatingSourceReadModelFlows
import slick.basic.DatabasePublisher
import pureconfig._

import scala.concurrent._

object RatingSourceReadModelUseCase {

  object Protocol {

    sealed trait RatingRequest

    sealed trait RatingResponse

    case class FetchRatingRequest(flagRecordId: Long) extends RatingRequest

    case class RatingTransaction(id: Long, tranDoc: String)

    case class FetchResultResponse(size: Int) extends RatingResponse

  }

}

class RatingSourceReadModelUseCase(ratingSourceReadModelFlows: RatingSourceReadModelFlows)
  (implicit val system: ActorSystem) extends UseCaseSupport {

  import RatingSourceReadModelUseCase.Protocol._
  import UseCaseSupport._

  private val config = loadConfigOrThrow[RatingAggregateUseCaseConfig]("rating.use-case.rating-use-case")

  private val bufferSize: Int = config.bufferSize

  implicit val mat: Materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  private val fetchRatingRequestQueue: SourceQueueWithComplete[(FetchRatingRequest, Promise[FetchResultResponse])] =
    Source.queue[(FetchRatingRequest, Promise[FetchResultResponse])](bufferSize, OverflowStrategy.dropNew)
      .via(ratingSourceReadModelFlows.fetchRatingFlow.zipPromise)
      .toMat(completePromiseSink)(Keep.left)
      .run()

  def fetchRating(fetchRatingRequest: FetchRatingRequest)(implicit ec: ExecutionContext): Future[FetchResultResponse] =
    offerToQueue(fetchRatingRequestQueue)(request = fetchRatingRequest, Promise())



}
