package net.dream.erp.interfaces.serialization

import java.nio.charset.StandardCharsets

import akka.event.LoggingAdapter
import io.circe._
import io.circe.parser._
import io.circe.syntax._

object StringToByteConversion {

  implicit class StringToByte(text: String) {
    def toUTF8Byte: Array[Byte] = text.getBytes(StandardCharsets.UTF_8)
  }

}

trait ObjToJsonReprIso[Object, JsonRepr] {
  def convertTo(obj: Object): JsonRepr
  def convertFrom(json: JsonRepr): Object
}

import net.dream.erp.interfaces.serialization.StringToByteConversion._

class CirceDeserializationException(message: String, cause: Throwable) extends Exception(message, cause)

object CirceJsonSerialization {

  def toBinary[Object, JsonRepr](
    orig: Object,
    isDebugEnabled: Boolean = false
  )(implicit iso: ObjToJsonReprIso[Object, JsonRepr], encoder: Encoder[JsonRepr], log: LoggingAdapter)={
    val obj         = iso.convertTo(orig)
    val jsonString  = obj.asJson.noSpaces

    if(isDebugEnabled)
      log.debug(s"toBinary: jsonString = $jsonString")
    jsonString.toUTF8Byte
  }

  def fromBinary[Object, JsonRepr](
    bytes: Array[Byte],
    isDebugEnabled: Boolean = false
  )(implicit iso: ObjToJsonReprIso[Object, JsonRepr], decoder: Decoder[JsonRepr], log: LoggingAdapter)= {
    val jsonString    = new String(bytes, StandardCharsets.UTF_8)
    if(isDebugEnabled)
      log.debug(s"fromBinary: jsonString = $jsonString")

    val result = for {
      json        <- parse(jsonString).right
      resultJson  <- json.as[JsonRepr].right
    } yield iso.convertFrom(resultJson)

    result match {
      case Left(failure)  => throw new CirceDeserializationException(failure.getMessage, failure)
      case Right(obj)     => obj
    }

  }

}
