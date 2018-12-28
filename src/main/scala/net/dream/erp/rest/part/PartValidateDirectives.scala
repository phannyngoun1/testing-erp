package net.dream.erp.rest.part

import java.util.Currency

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._
import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import net.dream.erp.domain.inventory.Part.PartType
import net.dream.erp.rest.RestSupport.ControllerBase._
import net.dream.erp.rest.RestSupport._
import net.dream.erp.rest.common.ValidateDirectives
import net.dream.erp.rest.common.ValidateDirectives._
import net.dream.erp.rest.part.PartRestSrv._
import org.sisioh.baseunits.scala.money.Money

object PartValidateDirectives {

  case class PartTypeError(message: String, cause: Option[Throwable] = None) extends Error

  def validatePartId(value: String): Validated[Error, Long] = {
    value.decodeFromHashId match {
      case Left(error) => IdFormatError("The id format is invalid.", Some(error)).invalid
      case Right(result) => result.valid
    }
  }

  def validateMoney(amount: Long, currencyCode: String): ValidationResult[Money] = {
    try {
      Money(BigDecimal(amount), Currency.getInstance(currencyCode)).validNel
    } catch {
      case ex: Throwable => MoneyError("", Some(ex)).invalidNel
    }
  }

  def validatePartType(value: Int): ValidationResult[PartType] = {
    try {
      PartType.withValue(value).validNel
    } catch {
      case ex: Throwable => PartTypeError("", Some(ex)).invalidNel
    }
  }

  def validatePartNr(value: String): ValidationResult[String] = {
    if (value.isEmpty || value.length > 255)
      RequiredError("The part number is empty or 255 length over.").invalidNel
    else value.validNel
  }

  def validateDescription(value: String): ValidationResult[String] = {
    if (value.isEmpty || value.length > 500)
      RequiredError("The part number is empty or 500 length over.").invalidNel
    else value.validNel
  }

  implicit object NewPartRequestJsonValidator extends Validator[NewPartRequestJson] {
    override def validate(value: NewPartRequestJson): ValidationResult[NewPartRequestJson] =
      (
        validatePartType(value.partTypeId),
        validatePartNr(value.partNr),
        validateDescription(value.description)
      ).mapN {
        case (_ : PartType, _: String, _: String) => value
      }
  }

  implicit object UpdatePartRequestJsonValidator extends Validator[UpdatePartRequestJson] {
    override def validate(value: UpdatePartRequestJson): ValidationResult[UpdatePartRequestJson] =
      (
        validatePartType(value.partTypeId),
        validatePartNr(value.partNr),
        validateDescription(value.description)
      ).mapN {
        case (_: PartType, _: String, _: String) => value
      }
  }



}

trait PartValidateDirectives extends ValidateDirectives {

  protected def validatePartId(value: String): Directive1[Long] = {
    PartValidateDirectives.validatePartId(value)
      .fold({ error =>
        reject(ValidationsRejection(NonEmptyList.of(error)))
      }, provide)
  }


}
