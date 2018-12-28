package net.dream.erp.usercase.rating.port

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import net.dream.erp.usercase.rating.RatingSourceReadModelUseCase.Protocol.{FetchRatingRequest, FetchResultResponse, RatingTransaction}

import scala.concurrent.ExecutionContext

trait RatingSourceReadModelFlows {
  def fetchRatingSource: Source[RatingTransaction, NotUsed]
  def fetchRatingFlow(implicit ec: ExecutionContext): Flow[FetchRatingRequest, FetchResultResponse, NotUsed]
}
