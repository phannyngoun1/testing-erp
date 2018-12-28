package net.dream.erp.rest

import akka.http.javadsl.server.CustomRejection
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.RejectionHandler
import cats.data.NonEmptyList
import cats.syntax.either._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import org.hashids.Hashids

object RestSupport {

  trait Error {
    val message: String
    val cause: Option[Throwable]
  }

  case class IdFormatError(message: String, cause: Option[Throwable] = None) extends Error

  case class MoneyError(message: String, cause: Option[Throwable] = None) extends Error


  trait ResponseJson {
    val isSuccessful: Boolean
    val errorMessages: Seq[String]
  }

  case class ValidationErrorsResponseJson(errorMessages: Seq[String]) extends ResponseJson {
    override val isSuccessful: Boolean = false
  }

  case class ValidationsRejection(errors: NonEmptyList[Error]) extends CustomRejection

  object RejectionHandlers {

    final val default: RejectionHandler = RejectionHandler
      .newBuilder()
      .handle {
        case ValidationsRejection(errors) =>
          complete((StatusCodes.BadRequest, ValidationErrorsResponseJson(errors.map(_.message).toList)))
      }
      .result()
  }

  object ControllerBase {

    implicit val hashIds: Hashids = new Hashids("salt")

    implicit class HashIdsStringOps(val self: String) extends AnyVal {
      def decodeFromHashId(implicit hashIds: Hashids): Either[Throwable, Long] = {
        Either.catchNonFatal(hashIds.decode(self)(0))
      }
    }

    implicit class HashIdsLongOps(val self: Long) extends AnyVal {
      def encodeToHashId(implicit hashIds: Hashids): Either[Throwable, String] =
        Either.catchNonFatal(hashIds.encode(self))
    }

  }

}
