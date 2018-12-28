package net.dream.erp.domain.inventory

import net.dream.erp.domain.inventory.NewPart._
import net.dream.erp.domain.inventory.Part.{PartSize, PartType, PartWeight}
import net.dream.erp.domain.inventory.PartTracking.TrackingType
import net.dream.erp.domain.location.Location
import org.sisioh.baseunits.scala.money.Money
import org.sisioh.baseunits.scala.time.TimePoint
import org.sisioh.baseunits.scala.timeutil.Clock

object InventoryEvent {


  sealed trait InventoryEvent {

    val inventoryId: InventoryId
    val occurredAt: TimePoint

  }

  case class AddNewPartEvent(
    inventoryId: InventoryId,
    partNr: String,
    description: String,
    upc: Option[String],
    partTypeId: Int,
    uomId: Int,
    productInfo: Option[ProductInfo],
    trackingTypes: List[TrackingType] = List.empty,

    size: Option[PartSize] = None,
    weight: Option[PartWeight] = None,

    initialInventory: Option[InitialInventory],
    defaultLocations: Option[List[Location]],
    defaultVendor: Option[DefaultVendor],
    defaultPartAccount: Option[DefaultPartAccount],
    defaultProductAccount: Option[DefaultProductAccount],
    override val occurredAt: TimePoint = Clock.now

  ) extends InventoryEvent

  case class InventoryInitialEvent(
    inventoryId: InventoryId,
    location: Int,
    uoM: Int,
    qty: Float,
    unitCost: Money,
    override val occurredAt: TimePoint = Clock.now,
  ) extends InventoryEvent


}