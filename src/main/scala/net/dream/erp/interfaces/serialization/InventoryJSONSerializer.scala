package net.dream.erp.interfaces.serialization

import akka.actor.ExtendedActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.serialization._
import net.dream.erp.domain.inventory.InventoryEvent.AddNewPartEvent
import net.dream.erp.domain.inventory.InventoryModel
import net.dream.erp.interfaces.service.inventory.InventoryService.Protocol._
import pureconfig._
//InventoryJSONManifest
object InventoryJSONManifest {
  final val ADD_NEW_PART = AddNewPartRequest.getClass.getName.stripSuffix("$")
  final val ADD_NEW_PART_EVENT = AddNewPartEvent.getClass.getName.stripSuffix("$")

  final val INVENTORY_MODEL = InventoryModel.getClass.getName.startsWith("$")

}

class InventoryJSONSerializer(system: ExtendedActorSystem) extends SerializerWithStringManifest {

  import InventoryCreatedJson._
  import InventoryJSONManifest._
  import io.circe.generic.auto._

  private implicit val logger: LoggingAdapter = Logging.getLogger(system, getClass)

  private val config = loadConfigOrThrow[InventoryObjectJSONSerializerConfig](
    system.settings.config.getConfig("inventory.interface.inventory-event-json-serializer")
  )

  private val isDebugEnabled = config.isDebuged

  override def identifier: Int = 50

  override def manifest(o: AnyRef): String = {
    val result = o.getClass.getName
    logger.debug(s"manifest: $result")
    result
  }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case orig: AddNewPartRequest  => CirceJsonSerialization.toBinary(orig, isDebugEnabled)
    case orig: AddNewPartEvent    => CirceJsonSerialization.toBinary(orig, isDebugEnabled)
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    logger.debug(s"fromBinary: $manifest")
    manifest match {
      case ADD_NEW_PART           => CirceJsonSerialization.fromBinary[AddNewPartRequest, AddNewPartRequestJson](bytes, isDebugEnabled)
      case ADD_NEW_PART_EVENT     => CirceJsonSerialization.fromBinary[AddNewPartEvent, AddNewPartEventJson](bytes, isDebugEnabled)
    }
  }


}
