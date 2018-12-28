package net.dream.erp.domain.inventory

import net.dream.erp.domain.inventory.InventoryModel.InventoryError
import net.dream.erp.domain.inventory.Part.{PartSize, PartType}
import net.dream.erp.domain.inventory.PartTracking.{TrackingType, TrackingValue}
import net.dream.erp.domain.location.Location
import org.sisioh.baseunits.scala.money.Money
import org.sisioh.baseunits.scala.time.TimePoint
import org.sisioh.baseunits.scala.timeutil.Clock


/**
  * Inventory status terms:
  *
  * On Hand - The total amount of inventory in stock.
  * Allocated - The amount of inventory allocated or assigned to other orders. For example, inventory on issued sales orders, purchase orders, transfer orders, and work orders. Hover over the Allocated quantity to see more details.
  * Not Available - The quantity in locations that are not available for sale.
  * Drop Ship - The quantity being drop shipped directly to customers.
  * Available For Sale - The quantity available for sale is calculated as On Hand - Allocated - Not Available + Drop Ship.
  * On Order - The quantity on order for a purchase order, sales order, work order, or transfer order.
  * Committed - Items picked and ready to be shipped, or items started on a work order. Inventory that is committed will always be allocated. However, inventory that is allocated is not always committed.
  * Short - The quantity needed to fulfill demand.
  * Available To Pick - Calculated as On Hand - Committed for inventory in locations that are marked as Pickable.
  */

trait InventoryModel {

  def id: InventoryId //equal part id

  def avgCosting: Option[AvgCosting]

  def stdCosting: Option[StdCosting]

  def costLayers: Set[CostLayer]

  def stocks: List[Stock]

  def onHand: Float

  def allocated: Float

  def committed: Float

  def availableForSale: Float

  def availableToPickup: Float

  def onOrder: Float

  def notAvailable: Float

  def dropShip: Float

  def sort: Float

  def isActive: Boolean

  def initStock(location: Location, uoM: UoM, qty: Float ,  unitCost: Money, date: TimePoint, partTracking: Option[TrackingValue] = None): Either[InventoryError, InventoryModel]

}

case class PartSizeState(length: Float, width: Float, height: Float, uomId: Int)
case class PartWeightState(weight: Float, uomId: Int)
case class PartAccountsState(assetAcctId: Int, cogsAcctId: Int, adjustmentAcctId: Int, scrapAcctId: Int)
case class PartVendorState(vendorId: Int, partNr: Option[String], lastCost: Option[Money], uomId: Int, createdAt: TimePoint, default: Boolean)

case class  PartState (
  id: Long,
  nr: String,
  description: String,
  upc: Option[String] = None,
  partType: PartType,
  uomId: Int,
  trackingTypes: List[TrackingType] = List.empty,
  size: Option[PartSizeState] = None,
  weight: Option[PartWeightState] = None,
  partUoMPickupOnly: Boolean = false,
  url: Option[String] = None,
  details: Option[String] = None,
  accounts: Option[PartAccountsState] = None,
  defaultLocationsId: List[Int] = List.empty,
  defaultVendors: List[PartVendorState] = List.empty,

  createdAt: TimePoint = Clock.now,
  modifiedAt: TimePoint = Clock.now,
  lastUserId: Int = 1,
)

case class SubstituteProductState(
  product: Product, note: String
)

case class ProductState(
  id: Long,
  nr: String,
  description: String,
  uomId: Int,
  price: Option[Money],
  soldInDiffUoM: Boolean = true,
  taxable: Boolean = true,
  details: Option[String] = None,
  upc: Option[String] = None,
  sku: Option[String] = None
//  soItemType: Option[Int] = None,
//  substituteProd: Option[List[SubstituteProduct]] = None,
//  incomeAccount: Option[Account] = None
)

object InventoryModel {

  sealed abstract class InventoryError(val message: String)

  case class InvalidStateError(id: Option[InventoryId] = None)
    extends InventoryError(s"Invalid state${id.fold("")(id => s":id = ${id.partId}")}")

  def apply(
    id: InventoryId,
    products: List[Product] = List.empty,
    avgCosting: Option[AvgCosting] = None,
    stdCosting: Option[StdCosting] = None,
    costLayers: Set[CostLayer] = Set.empty,
    stocks: List[Stock] = List.empty

  ): InventoryModel = new InventoryModelImpl(id, avgCosting, stdCosting, costLayers, stocks)

  def unapply(self: InventoryModel): Option[(InventoryId, Option[AvgCosting], Option[StdCosting], Set[CostLayer], List[Stock])] =
    Some(self.id, self.avgCosting, self.stdCosting, self.costLayers, self.stocks)

  private case class InventoryModelImpl(
    id: InventoryId,
    avgCosting: Option[AvgCosting],
    stdCosting: Option[StdCosting],
    costLayers: Set[CostLayer],
    stocks: List[Stock],
    active: Boolean = true

  ) extends InventoryModel {

    override def allocated: Float = ???

    override def committed: Float = ???

    override def availableForSale: Float = ???

    override def availableToPickup: Float = ???

    override def onOrder: Float = ???

    override def notAvailable: Float = ???

    override def dropShip: Float = ???

    override def sort: Float = ???

    override def onHand: Float = stocks.map(_.qty).reduceLeft( _ + _ )

    override def isActive: Boolean = active

    override def initStock(location: Location, uoM: UoM, qty: Float ,  unitCost: Money, date: TimePoint, partTrackingValue: Option[TrackingValue] = None):
    Either[InventoryError, InventoryModel] = {

      Right(copy(
        stocks = Stock(location, uoM, qty, date, partTrackingValue) :: stocks,
        avgCosting = Some(AvgCosting(unitCost, unitCost.*(BigDecimal(qty)) , date)),
        costLayers = costLayers + CostLayer(unitCost, unitCost.*(BigDecimal(qty)), date)
      ))
    }
  }

}
