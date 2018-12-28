package net.dream.erp.domain.inventory

import net.dream.erp.domain.accounting.Account
import net.dream.erp.domain.inventory.NewPart._
import net.dream.erp.domain.inventory.Part._
import net.dream.erp.domain.inventory.PartTracking.{TrackingType, TrackingValue}
import net.dream.erp.domain.location.Location
import net.dream.erp.domain.vendor.Vendor
import org.sisioh.baseunits.scala.money.Money
import org.sisioh.baseunits.scala.time.TimePoint

object NewPart {

  case class InitialInventory(loc: Location, qty: Float, uoM: UoM, unitCost: Money, postDate: TimePoint, trackingValues: Option[List[TrackingValue]] = None)

  case class ProductInfo(nr: String, description: String, upc: Option[String], sku: Option[String], price: Option[Money])

  case class DefaultVendor(vendor: Vendor, vendorPartNr: Option[String], vendorPartUoM: UoM, lastCost: Option[Money])

  case class DefaultPartAccount(assetAccount: Account, cogsAccount: Account, adjAccount: Account, scrapAccount: Account)

  case class DefaultProductAccount(incomeAccount: Account)

}

case class NewPart(
  partNr: String,
  description: String,
  upc: Option[String],
  partType: PartType,
  uom: UoM,
  productInfo: Option[ProductInfo],
  trackingTypes: List[TrackingType] =List.empty,

  size: Option[PartSize] = None,
  weight: Option[PartWeight] = None,

  initialInventory: Option[InitialInventory],
  defaultLocations: Option[List[Location]],
  defaultVendor: Option[DefaultVendor],
  defaultPartAccount: Option[DefaultPartAccount],
  defaultProductAccount: Option[DefaultProductAccount]
)
