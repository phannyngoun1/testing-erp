package net.dream.erp.domain.inventory

import java.net.URL

import enumeratum.values._
import net.dream.erp.domain.User.User
import net.dream.erp.domain.User.User._
import net.dream.erp.domain.accounting.Account
import net.dream.erp.domain.inventory.Part._
import net.dream.erp.domain.inventory.PartTracking.TrackingType
import net.dream.erp.domain.inventory.UoM._
import net.dream.erp.domain.location.Location
import net.dream.erp.domain.vendor.Vendor
import org.sisioh.baseunits.scala.money.Money
import org.sisioh.baseunits.scala.time.TimePoint
import org.sisioh.baseunits.scala.timeutil.Clock

object Part {


  /** Part types:
    *
    * Inventory - Can be purchased and sold and stocked in inventory.
    * Service - Can be purchased and sold but is not stocked in inventory.
    * Labor - Cannot be purchased or sold, but can be added during manufacturing or reconciling.
    * Overhead - Can be included as a component of a part's cost during manufacturing.
    * Non-Inventory - Can be purchased and sold but is not tracked in inventory.
    * Internal Use - Can be purchased but is not sold or stocked in inventory.
    * Capital Equipment - Equipment that depreciates in value and can be purchased but is not sold or stocked in inventory.
    * Shipping - Can be purchased or sold, but is not stocked in inventory.
    */
  sealed abstract class PartType(val value: Int, val name: String) extends IntEnumEntry

  case object PartType extends IntEnum[PartType] {

    val values = findValues

    case object Inventory extends PartType(1, "Inventory")

    case object Service extends PartType(2, "Service")

    case object Labor extends PartType(3, "Labor")

    case object Overhead extends PartType(4, "Overhead")

    case object NonInventory extends PartType(5, "None-Inventory")

    case object InternalUsed extends PartType(6, "Internal Used")

    case object CapitalEquipment extends PartType(7, "Capital Equipment")

    case object Shipping extends PartType(8, "Shipping")

  }


  case class PartSize(length: Float, width: Float, height: Float, uoM: UoM) {
    require(uoM match {
      case UoM(_, _, _, _, Measurement.Length, _, _) => true
      case _ => false
    })
  }

  case class PartWeight(weight: Float, uoM: UoM) {
    require(uoM match {
      case UoM(_, _, _, _, Measurement.Weight, _, _) => true
      case _ => false
    })
  }

  case class PartAccounts(asset: Account, cogs: Account, adjustment: Account, scrap: Account)

  case class PartVendor(
    vendor: Vendor,
    partNr: Option[String],
    lastCost: Option[Money],
    uoM: UoM,
    createdAt: TimePoint,
    default: Boolean
  )

}

case class Part(
  id: Long,
  nr: String,
  description: String,
  upc: Option[String] = None,
  partType: PartType,
  uom: UoM,
  trackingTypes: List[TrackingType] = List.empty,
  size: Option[PartSize] = None,
  weight: Option[PartWeight] = None,
  partUoMPickupOnly: Boolean = false,
  url: Option[URL] = None,
  details: Option[String] = None,
  accounts: Option[PartAccounts] = None,
  defaultLocations: List[Location] = List.empty,
  defaultVendors: List[PartVendor] = List.empty,

  createdAt: TimePoint = Clock.now,
  modifiedAt: TimePoint = Clock.now,
  lastUser: User = admin,

  active: Boolean = true

) {
  require(!nr.isEmpty)

}
