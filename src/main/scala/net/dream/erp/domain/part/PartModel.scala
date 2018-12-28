package net.dream.erp.domain.part

import akka.japi.Option.Some
import net.dream.erp.domain.part.PartEvent.{NewPartEvent, UpdatePartEvent}
import net.dream.erp.domain.part.PartModel.PartError
import org.sisioh.baseunits.scala.time.TimePoint
import org.sisioh.baseunits.scala.timeutil.Clock

trait PartModel {
  def id: Long
  def nr: String
  def description: String
  def partTypeId: Int
  def uomId: Int
  def isActive: Boolean

  def updatePart(event: UpdatePartEvent, occurredAt: TimePoint = Clock.now): Either[PartError, PartModel]
  def disablePart(occurredAt: TimePoint = Clock.now): Either[PartError, PartModel]
}

object PartModel {

  case class PartSizeState(length: Float, width: Float, height: Float, uomId: Int)

  case class PartWeightState(weight: Float, uomId: Int)

  case class PartAccountsState(assetAcctId: Int, cogsAcctId: Int, adjustmentAcctId: Int, scrapAcctId: Int)

  case class PartVendorState(vendorId: Int, partNr: Option[String], lastCost: BigDecimal, currency: String, uomId: Int, createdAt: Long, default: Boolean)

  sealed abstract class PartError(val message: String)

  case class DefaultPartError(override val message: String) extends PartError(message)

  case class InvalidPartStateError(id: Option[Long] = None)
    extends PartError(s"Invalid state${id.fold("")(id => s":id = ${id}")}")

  case class AlreadyInactiveStateError(id:  Option[Long] = None) extends PartError(s"Part ${id.fold("")(id => s":id = ${id}")} is already inactive")

  def apply(
    newEvent: NewPartEvent
  ): PartModel = new PartModelImpl(
    id = newEvent.id,
    nr = newEvent.nr,
    description = newEvent.description,
    partTypeId = newEvent.partTypeId,
    uomId = newEvent.uomId
  )

  def unapply(self: PartModel): Option[(Long, String, String, Int, Int)] =
    Some((self.id, self.nr, self.description, self.partTypeId, self.uomId))

  private case class PartModelImpl(
    id: Long,
    nr: String,
    description: String,
    upc: Option[String] = None,
    partTypeId: Int,
    uomId: Int,
    trackingTypes: List[Int] = List.empty,
    size: Option[PartSizeState] = None,
    weight: Option[PartWeightState] = None,
    partUoMPickupOnly: Boolean = false,
    url: Option[String] = None,
    details: Option[String] = None,
    accounts: Option[PartAccountsState] = None,
    defaultLocationsId: List[Int] = List.empty,
    defaultVendors: List[PartVendorState] = List.empty,
    createdAt: Long = Clock.now.millisecondsFromEpoc,
    modifiedAt: Long = Clock.now.millisecondsFromEpoc,
    lastUserId: Int = 1,
    isActive: Boolean = true
  ) extends PartModel {
    override def updatePart(event: UpdatePartEvent, occurredAt: TimePoint): Either[PartError, PartModel] = {
      Right(copy(
        nr          = event.nr,
        description = event.description,
        partTypeId  = event.partTypeId,
        uomId       = event.uomId
      ))
    }

    override def disablePart(occurredAt: TimePoint): Either[PartError, PartModel] = {
      if(!isActive)
        Left(AlreadyInactiveStateError(Some(id)))
      else
        Right(copy(
          isActive = false
        ))

    }
  }

}
