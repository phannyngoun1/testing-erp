package net.dream.erp.rest.inventory

import net.dream.erp.rest.common.ValidateDirectives
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._
import net.dream.erp.rest.RestSupport.ControllerBase._
import net.dream.erp.rest.RestSupport._
import cats.data.{NonEmptyList, Validated}
import cats.implicits._

object InventoryValidateDirectives {

  def validatePartId(value: String): Validated[Error, Long] = {
    value.decodeFromHashId match {
      case Left(error) => IdFormatError("The id format is invalid.", Some(error)).invalid
      case Right(result) => result.valid
    }
  }
}

trait InventoryValidateDirectives extends ValidateDirectives {

  protected def validatePartId(value: String): Directive1[Long] = {
    InventoryValidateDirectives.validatePartId(value)
      .fold({ error =>
        reject(ValidationsRejection(NonEmptyList.of(error)))
      }, provide)
  }

}


