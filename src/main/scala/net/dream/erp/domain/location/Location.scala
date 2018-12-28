package net.dream.erp.domain.location

import enumeratum.values._
import net.dream.erp.domain.User.User
import net.dream.erp.domain.location.Location.{LocationTerm, LocationType}


object Location {

  /** Location types:
    *
    * Consignment - Used to store inventory that is on consignment.
    * In Transit - Used to store inventory that is being moved by a transfer order.
    * Inspection - Could be used to store inventory that is being inspected.
    * Locked - Could be used to designate a secure warehouse area.
    * Manufacturing - Used to store inventory that is being used on a manufacture order. By default, inventory picked for a work order will be moved to the default manufacturing location for the specified location group.
    * Picking - Could be used to store inventory that is being picked.
    * Receiving - Used to store inventory that is received in the Small.Receiving.png Receiving module. By default, inventory purchased from a vendor will be received into the default receiving location for the specified location group, unless the part has a default location.
    * Shipping - Used to store inventory that is ready to be shipped. By default, inventory that has been picked will be stored in the default shipping location for the specified location group.
    * Stock - Used to store inventory in the warehouse. Typically, inventory will be picked from a stock location, unless the part has a default location.
    * Store Front - Could be used to store inventory in a location that is accessible to customers.
    * Vendor - Could be used to store inventory that is associated with a vendor.
    */
  sealed abstract class LocationType(val value: Int, val name: String) extends IntEnumEntry

  case object LocationType extends IntEnum[LocationType] {

    case object StockLoc extends LocationType(1, "Stock")

    case object FontStoreLoc extends LocationType(2, "Front Store")

    case object VendorLoc extends LocationType(3, "Vendor")

    case object ReceivingLoc extends LocationType(4, "Receiving")

    case object ManufacturingLoc extends LocationType(5, "Manufacturing")

    case object LockedLoc extends LocationType(6, "Locked")

    case object InspectionLoc extends LocationType(7, "Inspection")

    case object InTransitLoc extends LocationType(8, "In Transit")

    case object ConsignmentLoc extends LocationType(9, "Consignment")

    case object PickingLoc extends LocationType(10, "Picking")

    case object ShippingLoc extends LocationType(11, "Shipping")

    val values = findValues
  }

  /** Location terms:
    *
    * Default location for this location type and group - Sets the location as the default for a given location type and location group.
    * Available for Sale - Marks inventory in this location as available for sale. If this option isn't selected, inventory in this location will be classified as Not Available.
    * Active - Sets the location as available for moving, picking, receiving, etc. Uncheck this box if the location should no longer be used for these actions. This overrides the pickable and receivable options. For example, there cannot be an inactive pickable location.
    * Pickable - Marks inventory in a location as pickable. Unchecking this box will prevent inventory in this location from being picked and exclude it from the Available To Pick total.
    * Receivable - Allows a location to receive inventory.
    */

  sealed abstract class LocationTerm(val value: Int, val name: String) extends IntEnumEntry

  object LocationTerm extends IntEnum[LocationTerm] {

    case object DefaultLocTerm extends LocationTerm(1, "Default location for this location type and group")

    case object AvailableForSaleTerm extends LocationTerm(2, "Available for Sale")

    case object ActiveTerm extends LocationTerm(3, "Active")

    case object PickableTerm extends LocationTerm(4, "Pickable")

    case object ReceivableTerm extends LocationTerm(5, "Receivable")

    val values = findValues
  }

}

case class GroupLocation(id: Int, name: String, description: String, users: List[User])

case class Location(id: Int, name: String, groupLocation: GroupLocation, locationType: LocationType, locationTerms: List[LocationTerm])
