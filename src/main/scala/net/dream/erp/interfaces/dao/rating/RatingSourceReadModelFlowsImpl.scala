package net.dream.erp.interfaces.dao.rating

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import net.dream.erp.usercase.rating.RatingSourceReadModelUseCase.Protocol._
import net.dream.erp.usercase.rating.port.RatingSourceReadModelFlows
import slick.jdbc.{JdbcProfile, ResultSetConcurrency, ResultSetType}

import scala.concurrent.ExecutionContext

class RatingSourceReadModelFlowsImpl(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
  extends RatingComponent with RatingComponentSupport with RatingSourceReadModelFlows {

  import profile.api._

  override def fetchRatingFlow(implicit ec: ExecutionContext): Flow[FetchRatingRequest, FetchResultResponse, NotUsed] =

    Flow[FetchRatingRequest]
      .mapAsync(1) { _request =>
        db.run(RatingDao.filter(_.id === 1l ).result )map((_request, _))
      }
      .map {
        case (_request, result) =>
          val values = result.map{ v =>
            v.id
          }
          FetchResultResponse(values.size)
      }

  override def fetchRatingSource: Source[RatingTransaction, NotUsed] = {
    val streamRs = db.stream(
      RatingDao
        .result
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = 10000)
        .transactionally)
    Source.fromPublisher(streamRs).map(rd => RatingTransaction(rd.id, rd.tranDoc))
  }


}
