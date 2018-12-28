package net.dream.erp.integrate

import io.circe._
import io.circe.generic.semiauto._
import net.dream.erp.rest.hello.HelloActor.Greeting

object JsonSerialize {

  implicit val greetingDecoder: Decoder[Greeting] = deriveDecoder
  implicit val greetingEncoder: Encoder[Greeting] = deriveEncoder

}
