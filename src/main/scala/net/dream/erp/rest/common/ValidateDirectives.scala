package net.dream.erp.rest.common

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{provide, reject}
import cats.data.ValidatedNel
import net.dream.erp.rest.RestSupport.{Error, ValidationsRejection}

object ValidateDirectives {

  case class RequiredError(message: String, cause: Option[Throwable] = None) extends Error

  type ValidationResult[A] = ValidatedNel[Error, A]

  trait Validator[A] {
    def validate(value: A): ValidationResult[A]
  }
}

trait ValidateDirectives {
  import ValidateDirectives._

  protected def validateJson[A: Validator](value: A): Directive1[A] =
    implicitly[Validator[A]]
      .validate(value)
      .fold({ errors =>
        reject(ValidationsRejection(errors))
      }, provide)
}
